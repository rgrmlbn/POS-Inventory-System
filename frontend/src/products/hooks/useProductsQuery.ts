import { productService } from "../services/productService";

// hooks/useProductsQuery.ts
import { keepPreviousData, useQuery } from "@tanstack/react-query";

export function useProductsQuery(productName: string, page: number, size: number) {
    const query = useQuery({
        queryKey: ["products", productName, page, size],
        queryFn: () => productService.getAllProductsByProductName(productName, page, size),
        placeholderData: keepPreviousData,
    });

    return {
        products: query.data?.content ?? [],
        totalPages: query.data?.totalPages ?? 0,
        loading: query.isLoading,
        error: query.error,
    };
}