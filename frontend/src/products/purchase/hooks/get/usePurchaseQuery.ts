import { useQuery } from "@tanstack/react-query";
import { PurchaseService } from "../../services/PurchaseService";

export function usePurchaseQuery(id: number) {
  const query = useQuery({
    queryKey: ["purchase", id],
    queryFn: () => PurchaseService.getPurchaseById(id),
  });

  return {
    purchase: query.data,
    loading: query.isLoading,
    error: query.error,
  };
}
