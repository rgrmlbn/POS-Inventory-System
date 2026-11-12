import axios from "axios"
import type { Category, CategoryPage } from "../types/Category"


const BASE_URL = import.meta.env.VITE_API_URL;

export const CategoryService = {

      async getAllCategoriesByCategoryName(
    categoryName: string,
    page: number = 0,
    size: number = 10,
  ) {
    const response = await axios.get<CategoryPage>(
      BASE_URL + "/api/v1/categories/categoryName",
      {
        params: {
          categoryName,
          page,
          size,
        },
      },
    );

    return response.data;
  },

  async getCategoryById(categoryId: number) {
    const response = await axios.get<Category>(
      BASE_URL + "/api/v1/categories/" + categoryId,
    );

    return response.data;
  },

};