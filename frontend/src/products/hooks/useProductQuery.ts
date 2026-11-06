import { useQuery } from "@tanstack/react-query";
import { productService } from "../services/productService";

export function useProductQuery(productId: number) {
    const query = useQuery({
        queryKey: ["product", productId],
        queryFn: () => productService.getProductsById(productId),
    });

    return {
        product: query.data,
        loading: query.isLoading,
        error: query.error,
    };
}