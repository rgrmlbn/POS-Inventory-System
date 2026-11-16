import axios from "axios";
import type { Sale, SalePage } from "../types/Sale";

const BASE_URL = import.meta.env.VITE_API_URL;

export const SaleService = {
  async getAllSalesByProductName(
    productName: string,
    page: number = 0,
    size: number = 10,
  ) {
    const response = await axios.get<SalePage>(
      BASE_URL + "/api/v1/sales/productName",
      {
        params: {
          productName,
          page,
          size,
        },
      },
    );

    return response.data;
  },

  async getSaleById(id: number) {
    const response = await axios.get<Sale>(
      BASE_URL + "/api/v1/sales/" + id,
    );

    return response.data;
  },
};
