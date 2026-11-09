import { useQuery } from "@tanstack/react-query";
import { ProductService } from "../services/ProductService";

export function useProductQuery(productId: number) {
  const query = useQuery({
    queryKey: ["product", productId],
    queryFn: () => ProductService.getProductById(productId),
  });

  return {
    product: query.data,
    loading: query.isLoading,
    error: query.error,
  };
}
