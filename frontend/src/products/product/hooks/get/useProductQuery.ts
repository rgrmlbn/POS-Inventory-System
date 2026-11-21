import { useQuery } from "@tanstack/react-query";
import { ProductService } from "../../services/ProductService";

export function useProductQuery(id: number) {
  const query = useQuery({
    queryKey: ["product", id],
    queryFn: () => ProductService.getProductById(id),
  });

  return {
    product: query.data,
    loading: query.isLoading,
    error: query.error,
  };
}
