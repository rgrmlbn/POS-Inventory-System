import axios from "axios";

import type { Dashboard } from "../types/Dashboard";

const BASE_URL = import.meta.env.VITE_API_URL;

export const DashboardService = {
  async getDashboardData() {
    const response = await axios.get<Dashboard>(
      BASE_URL + "/api/v1/dashboard/all-data",
    );

    return response.data;
  },
};
