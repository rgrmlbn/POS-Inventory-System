import { ProductService } from "../../services/ProductService";

import { useQuery } from "@tanstack/react-query";

export function useProductsQuery() {
  const query = useQuery({
    queryKey: ["products", "all"],
    queryFn: () => ProductService.getAllProducts(),
  });

  return {
    products: query.data ?? [],
    loading: query.isLoading,
    error: query.error,
  };
}
