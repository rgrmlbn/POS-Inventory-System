import { useQuery } from "@tanstack/react-query";
import { InventoryService } from "../services/InventoryService";

export function useInventoryQuery(productId: number) {
  const query = useQuery({
    queryKey: ["product", productId],
    queryFn: () => InventoryService.getInventoryByProductId(productId),
  });

  return {
    product: query.data,
    loading: query.isLoading,
    error: query.error,
  };
}
