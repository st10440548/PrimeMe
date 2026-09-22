namespace PrimeMeApi.Tests;

public class ApiTests
{
    [Fact]
    public void GoalTitle_ShouldNotBeEmpty()
    {
        var title = "Build PrimeMe";

        Assert.False(string.IsNullOrWhiteSpace(title));
    }

    [Fact]
    public void GoalProgress_ShouldBeBetweenZeroAndOneHundred()
    {
        var progress = 60;

        Assert.InRange(progress, 0, 100);
    }
}
