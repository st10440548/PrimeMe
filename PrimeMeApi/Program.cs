using Npgsql;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddCors(options =>
{
    options.AddPolicy("PrimeMe", policy =>
        policy.AllowAnyOrigin()
              .AllowAnyHeader()
              .AllowAnyMethod());
});

var app = builder.Build();
app.UseCors("PrimeMe");

var connectionString =
    Environment.GetEnvironmentVariable("AZURE_POSTGRESQL_CONNECTIONSTRING")
    ?? throw new InvalidOperationException(
        "AZURE_POSTGRESQL_CONNECTIONSTRING is not configured.");

await using (var connection = new NpgsqlConnection(connectionString))
{
    await connection.OpenAsync();

    await using var command = new NpgsqlCommand("""
        CREATE TABLE IF NOT EXISTS goals (
            id SERIAL PRIMARY KEY,
            title TEXT NOT NULL,
            progress INTEGER NOT NULL DEFAULT 0
        );
        """, connection);

    await command.ExecuteNonQueryAsync();

    await using var countCommand =
        new NpgsqlCommand("SELECT COUNT(*) FROM goals;", connection);

    var count = (long)(await countCommand.ExecuteScalarAsync() ?? 0);

    if (count == 0)
    {
        await using var seedCommand = new NpgsqlCommand("""
            INSERT INTO goals (title, progress)
            VALUES
                ('Run 10 km', 60),
                ('Read 2 books', 40),
                ('Learn piano', 25);
            """, connection);

        await seedCommand.ExecuteNonQueryAsync();
    }
}

app.MapGet("/", () => Results.Ok(new
{
    app = "PrimeMe API",
    status = "running",
    database = "PostgreSQL"
}));

app.MapGet("/api/goals", async () =>
{
    var goals = new List<Goal>();

    await using var connection = new NpgsqlConnection(connectionString);
    await connection.OpenAsync();

    await using var command = new NpgsqlCommand(
        "SELECT id, title, progress FROM goals ORDER BY id;",
        connection);

    await using var reader = await command.ExecuteReaderAsync();

    while (await reader.ReadAsync())
    {
        goals.Add(new Goal(
            reader.GetInt32(0),
            reader.GetString(1),
            reader.GetInt32(2)));
    }

    return Results.Ok(goals);
});

app.MapPost("/api/goals", async (CreateGoalRequest request) =>
{
    if (string.IsNullOrWhiteSpace(request.Title))
    {
        return Results.BadRequest(new
        {
            error = "Goal title is required."
        });
    }

    await using var connection = new NpgsqlConnection(connectionString);
    await connection.OpenAsync();

    await using var command = new NpgsqlCommand("""
        INSERT INTO goals (title, progress)
        VALUES (@title, 0)
        RETURNING id, title, progress;
        """, connection);

    command.Parameters.AddWithValue("title", request.Title.Trim());

    await using var reader = await command.ExecuteReaderAsync();
    await reader.ReadAsync();

    var goal = new Goal(
        reader.GetInt32(0),
        reader.GetString(1),
        reader.GetInt32(2));

    return Results.Created($"/api/goals/{goal.Id}", goal);
});

app.Run();

record Goal(int Id, string Title, int Progress);
record CreateGoalRequest(string Title);
