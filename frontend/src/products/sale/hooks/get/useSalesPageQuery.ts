import { SaleService } from "../../services/SaleService";

import { keepPreviousData, useQuery } from "@tanstack/react-query";

export function useSalesPageQuery(productName: string, page: number, size: number) {
  const query = useQuery({
    queryKey: ["sales", productName, page, size],
    queryFn: () =>
      SaleService.getAllSalesByProductName(productName, page, size),
    placeholderData: keepPreviousData,
  });

  return {
    sales: query.data?.content ?? [],
    totalPages: query.data?.totalPages ?? 0,
    loading: query.isLoading,
    error: query.error,
  };
}
