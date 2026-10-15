import { fireEvent, render, screen, waitFor, within } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import App from "./App";
import { getForecast, searchLocations } from "./api";

vi.mock("./api", () => ({
  getForecast: vi.fn(),
  searchLocations: vi.fn(),
}));

const city = {
  id: 1,
  name: "Copenhagen",
  region: "Capital Region",
  country: "Denmark",
  countryCode: "DK",
  latitude: 55.68,
  longitude: 12.57,
};

const forecast = {
  location: "Copenhagen",
  latitude: 55.68,
  longitude: 12.57,
  timezone: "Europe/Copenhagen",
  current: {
    time: "2025-06-01T12:00",
    temperature: 18.4,
    humidity: 62,
    feelsLike: 17.8,
    daytime: true,
    precipitation: 0,
    weatherCode: 2,
    windSpeed: 12,
  },
  daily: [
    {
      date: "2025-06-01",
      high: 21.2,
      low: 13.4,
      precipitationProbability: 10,
      weatherCode: 2,
    },
  ],
};

describe("Weather Atlas", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("searches for a city and displays its forecast", async () => {
    vi.mocked(searchLocations).mockResolvedValue([city]);
    vi.mocked(getForecast).mockResolvedValue(forecast);
    render(<App />);

    fireEvent.change(screen.getByLabelText("Where are you headed?"), {
      target: { value: "Copenhagen" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Search cities" }));

    const cityName = await screen.findByText("Copenhagen", { selector: "strong" });
    fireEvent.click(cityName.closest("button")!);

    expect(await screen.findByRole("heading", { name: "Copenhagen" })).toBeInTheDocument();
    expect(screen.getByText("Five-day outlook")).toBeInTheDocument();
    expect(screen.getByText("62%")).toBeInTheDocument();
    expect(within(screen.getByLabelText("Forecast for Copenhagen")).getByText("21°")).toBeInTheDocument();
  });

  it("shows a helpful message when a search has no results", async () => {
    vi.mocked(searchLocations).mockResolvedValue([]);
    render(<App />);

    fireEvent.change(screen.getByLabelText("Where are you headed?"), {
      target: { value: "Nowhere" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Search cities" }));

    await waitFor(() => expect(screen.getByRole("alert")).toHaveTextContent("No cities found"));
  });
});
