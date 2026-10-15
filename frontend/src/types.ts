export interface CityLocation {
  id: number | null;
  name: string;
  region: string | null;
  country: string;
  countryCode: string;
  latitude: number;
  longitude: number;
}

export interface CurrentConditions {
  time: string;
  temperature: number;
  humidity: number;
  feelsLike: number;
  daytime: boolean;
  precipitation: number;
  weatherCode: number;
  windSpeed: number;
}

export interface DailyForecast {
  date: string;
  high: number;
  low: number;
  precipitationProbability: number;
  weatherCode: number;
}

export interface WeatherForecast {
  location: string;
  latitude: number;
  longitude: number;
  timezone: string;
  current: CurrentConditions;
  daily: DailyForecast[];
}
