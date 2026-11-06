using FirebaseAdmin;
using FirebaseAdmin.Auth;
using Google.Apis.Auth.OAuth2;
using Npgsql;

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddCors(options => options.AddPolicy("PrimeMe", policy =>
    policy.AllowAnyOrigin().AllowAnyHeader().AllowAnyMethod()));

var connectionString = Environment.GetEnvironmentVariable("AZURE_POSTGRESQL_CONNECTIONSTRING")
    ?? throw new InvalidOperationException("AZURE_POSTGRESQL_CONNECTIONSTRING is not configured.");
var firebaseProjectId = Environment.GetEnvironmentVariable("FIREBASE_PROJECT_ID")
    ?? throw new InvalidOperationException("FIREBASE_PROJECT_ID is not configured.");
// Use a service-account credential from Azure App Settings; never commit the private key.
var firebaseCredentialJson = Environment.GetEnvironmentVariable("FIREBASE_SERVICE_ACCOUNT_JSON");
var firebaseCredential = !string.IsNullOrWhiteSpace(firebaseCredentialJson)
    ? GoogleCredential.FromJson(firebaseCredentialJson)
    : GoogleCredential.GetApplicationDefault();
FirebaseApp.Create(new AppOptions { Credential = firebaseCredential, ProjectId = firebaseProjectId });

var app = builder.Build();
app.UseCors("PrimeMe");

await using (var connection = new NpgsqlConnection(connectionString))
{
    await connection.OpenAsync();
    await using var command = new NpgsqlCommand("""
        CREATE TABLE IF NOT EXISTS goals (
            id SERIAL PRIMARY KEY,
            title TEXT NOT NULL,
            progress INTEGER NOT NULL DEFAULT 0
        );
        ALTER TABLE goals ADD COLUMN IF NOT EXISTS owner_uid TEXT;
        CREATE INDEX IF NOT EXISTS ix_goals_owner_uid ON goals(owner_uid);
        """, connection);
    await command.ExecuteNonQueryAsync();
    // Existing rows retain NULL owner_uid. Do not guess or transfer ownership.
}

app.MapGet("/", () => Results.Ok(new { app = "PrimeMe API", status = "running", database = "PostgreSQL" }));

// Every goals endpoint requires a valid Firebase ID token; the UID comes only from verification.
async Task<string?> VerifyUser(HttpRequest request)
{
    var authorization = request.Headers.Authorization.ToString();
    if (!authorization.StartsWith("Bearer ", StringComparison.OrdinalIgnoreCase)) return null;
    var token = authorization["Bearer ".Length..].Trim();
    if (string.IsNullOrWhiteSpace(token)) return null;
    try
    {
        var decoded = await FirebaseAuth.DefaultInstance.VerifyIdTokenAsync(token, true);
        return decoded.Uid;
    }
    catch (Exception)
    {
        return null;
    }
}

app.MapGet("/api/goals", async (HttpRequest request) =>
{
    var uid = await VerifyUser(request);
    if (uid is null) return Results.Unauthorized();
    var goals = new List<Goal>();
    await using var connection = new NpgsqlConnection(connectionString);
    await connection.OpenAsync();
    await using var command = new NpgsqlCommand(
        "SELECT id, title, progress FROM goals WHERE owner_uid = @uid ORDER BY id;", connection);
    command.Parameters.AddWithValue("uid", uid);
    await using var reader = await command.ExecuteReaderAsync();
    while (await reader.ReadAsync())
        goals.Add(new Goal(reader.GetInt32(0), reader.GetString(1), reader.GetInt32(2)));
    return Results.Ok(goals);
});

app.MapPost("/api/goals", async (HttpRequest request, CreateGoalRequest body) =>
{
    var uid = await VerifyUser(request);
    if (uid is null) return Results.Unauthorized();
    if (string.IsNullOrWhiteSpace(body.Title)) return Results.BadRequest(new { error = "Goal title is required." });
    await using var connection = new NpgsqlConnection(connectionString);
    await connection.OpenAsync();
    await using var command = new NpgsqlCommand("""
        INSERT INTO goals (title, progress, owner_uid)
        VALUES (@title, 0, @uid)
        RETURNING id, title, progress;
        """, connection);
    command.Parameters.AddWithValue("title", body.Title.Trim());
    command.Parameters.AddWithValue("uid", uid);
    await using var reader = await command.ExecuteReaderAsync();
    await reader.ReadAsync();
    var goal = new Goal(reader.GetInt32(0), reader.GetString(1), reader.GetInt32(2));
    return Results.Created($"/api/goals/{goal.Id}", goal);
});

app.MapPatch("/api/goals/{id:int}/complete", async (HttpRequest request, int id) =>
{
    var uid = await VerifyUser(request);
    if (uid is null) return Results.Unauthorized();
    await using var connection = new NpgsqlConnection(connectionString);
    await connection.OpenAsync();
    await using var command = new NpgsqlCommand("""
        UPDATE goals SET progress = 100
        WHERE id = @id AND owner_uid = @uid
        RETURNING id, title, progress;
        """, connection);
    command.Parameters.AddWithValue("id", id);
    command.Parameters.AddWithValue("uid", uid);
    await using var reader = await command.ExecuteReaderAsync();
    if (!await reader.ReadAsync()) return Results.NotFound();
    return Results.Ok(new Goal(reader.GetInt32(0), reader.GetString(1), reader.GetInt32(2)));
});

app.MapDelete("/api/goals/{id:int}", async (HttpRequest request, int id) =>
{
    var uid = await VerifyUser(request);
    if (uid is null) return Results.Unauthorized();
    await using var connection = new NpgsqlConnection(connectionString);
    await connection.OpenAsync();
    await using var command = new NpgsqlCommand(
        "DELETE FROM goals WHERE id = @id AND owner_uid = @uid;", connection);
    command.Parameters.AddWithValue("id", id);
    command.Parameters.AddWithValue("uid", uid);
    var deleted = await command.ExecuteNonQueryAsync();
    return deleted == 0 ? Results.NotFound() : Results.NoContent();
});

app.Run();

record Goal(int Id, string Title, int Progress);
record CreateGoalRequest(string Title);
