
import axios from "axios";
import type { Product, ProductPage } from "../types";

export const productService = {
    
    async getAllProducts(page:number = 0, size:number = 10) {
        
        const response = await axios.get<ProductPage>(
            "http://localhost:8080/api/v1/products",
            {
                params: {
                    page,   
                    size,
                }
            }
        );

        return response.data;
    },

    async getAllProductsByProductName(
        productName: string,
        page: number = 0,
        size: number = 10
    ) {

        const response = await axios.get<ProductPage>(
            "http://localhost:8080/api/v1/products/productName",
            {
                params: {
                    productName,
                    page,
                    size,
                }
            }
        );

        return response.data;
    },

    async getProductsById(productId:number) {
        
        const response = await axios.get<Product>(
            `http://localhost:8080/api/v1/products/${productId}`
        );

        return response.data;
        
    }
};
