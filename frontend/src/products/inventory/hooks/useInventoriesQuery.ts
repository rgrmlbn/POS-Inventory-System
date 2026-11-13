import { InventoryService } from "../services/InventoryService";

import { keepPreviousData, useQuery } from "@tanstack/react-query";

export function useInventoriesQuery(
  productName: string,
  page: number,
  size: number,
) {
  const query = useQuery({
    queryKey: ["inventories", productName, page, size],
    queryFn: () =>
      InventoryService.getAllInventoriesByProductName(productName, page, size),
    placeholderData: keepPreviousData,
  });

  return {
    inventories: query.data?.content ?? [],
    totalPages: query.data?.totalPages ?? 0,
    loading: query.isLoading,
    error: query.error,
  };
}
