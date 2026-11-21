import { useQuery } from "@tanstack/react-query";
import { SaleService } from "../../services/SaleService";

export function useSaleQuery(id: number) {
  const query = useQuery({
    queryKey: ["sale", id],
    queryFn: () => SaleService.getSaleById(id),
  });

  return {
    sale: query.data,
    loading: query.isLoading,
    error: query.error,
  };
}
