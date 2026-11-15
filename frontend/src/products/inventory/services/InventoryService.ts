import axios from "axios";
import type { Inventory, InventoryPage } from "../types/Inventory";

const BASE_URL = import.meta.env.VITE_API_URL;

export const InventoryService = {
  async getAllInventoriesByProductName(
    productName: string,
    page: number = 0,
    size: number = 10,
  ) {
    const response = await axios.get<InventoryPage>(
      BASE_URL + "/api/v1/inventories/productName",
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

  async getInventoryById(id: number) {
    const response = await axios.get<Inventory>(
      BASE_URL + "/api/v1/inventories/" + id,
    );

    return response.data;
  },
};
