import { SaleService } from "../../services/SaleService";

import { useQuery } from "@tanstack/react-query";

export function useSalesQuery() {
  const query = useQuery({
    queryKey: ["sales", "all"],
    queryFn: () => SaleService.getAllSales()
  });

  return {
    sales: query.data ?? [],
    loading: query.isLoading,
    error: query.error,
  };
}
