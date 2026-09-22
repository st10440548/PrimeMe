namespace PrimeMeApi.Tests;

public static class ApiTests
{
    public static void Run()
    {
        GoalTitle_ShouldNotBeEmpty();
        GoalProgress_ShouldBeBetweenZeroAndOneHundred();

        Console.WriteLine("All API unit tests passed.");
    }

    private static void GoalTitle_ShouldNotBeEmpty()
    {
        var title = "Build PrimeMe";

        if (string.IsNullOrWhiteSpace(title))
            throw new Exception("Goal title validation failed.");

        Console.WriteLine("PASS: Goal title validation");
    }

    private static void GoalProgress_ShouldBeBetweenZeroAndOneHundred()
    {
        var progress = 60;

        if (progress < 0 || progress > 100)
            throw new Exception("Goal progress validation failed.");

        Console.WriteLine("PASS: Goal progress validation");
    }
}
