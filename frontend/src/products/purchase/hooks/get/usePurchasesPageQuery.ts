import { PurchaseService } from "../../services/PurchaseService";

import { keepPreviousData, useQuery } from "@tanstack/react-query";

export function usePurchasesPageQuery(
  productName: string,
  page: number,
  size: number,
) {
  const query = useQuery({
    queryKey: ["purchases", productName, page, size],
    queryFn: () =>
      PurchaseService.getAllPurchasesByProductName(productName, page, size),
    placeholderData: keepPreviousData,
  });

  return {
    purchases: query.data?.content ?? [],
    totalPages: query.data?.totalPages ?? 0,
    loading: query.isLoading,
    error: query.error,
  };
}
