var builder = WebApplication.CreateBuilder(args);

builder.Services.AddCors(options =>
{
    options.AddPolicy("PrimeMe", policy =>
    {
        policy.AllowAnyOrigin()
              .AllowAnyHeader()
              .AllowAnyMethod();
    });
});

var app = builder.Build();

app.UseCors("PrimeMe");

var goals = new List<Goal>
{
    new Goal(1, "Run 10 km", 60),
    new Goal(2, "Read 2 books", 40),
    new Goal(3, "Learn piano", 25)
};

app.MapGet("/", () => Results.Ok(new
{
    app = "PrimeMe API",
    status = "running"
}));

app.MapGet("/api/goals", () =>
{
    return Results.Ok(goals);
});

app.MapPost("/api/goals", (CreateGoalRequest request) =>
{
    if (string.IsNullOrWhiteSpace(request.Title))
    {
        return Results.BadRequest(new
        {
            error = "Goal title is required."
        });
    }

    var goal = new Goal(
        goals.Count + 1,
        request.Title.Trim(),
        0
    );

    goals.Add(goal);

    return Results.Created(
        $"/api/goals/{goal.Id}",
        goal
    );
});

app.Run();

record Goal(int Id, string Title, int Progress);

record CreateGoalRequest(string Title);
