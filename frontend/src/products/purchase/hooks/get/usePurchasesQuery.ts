import { PurchaseService } from "../../services/PurchaseService";

import { useQuery } from "@tanstack/react-query";

export function usePurchasesQuery() {
  const query = useQuery({
    queryKey: ["purchases", "all"],
    queryFn: () => PurchaseService.getAllPurchases(),
  });

  return {
    purchases: query.data ?? [],
    loading: query.isLoading,
    error: query.error,
  };
}
