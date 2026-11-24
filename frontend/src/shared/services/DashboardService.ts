import axios from "axios";

export type ProductCountResponse = {
  productCount: number;
};

const BASE_URL = import.meta.env.VITE_API_URL;

export const DashboardService = {
  async getProductCount() {
    const response = await axios.get<ProductCountResponse>(
      BASE_URL + "/api/v1/dashboard/product-count",
    );

    return response.data;
  },
};
