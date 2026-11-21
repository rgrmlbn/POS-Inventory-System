import { useQuery } from "@tanstack/react-query";
import { InventoryService } from "../../services/InventoryService";

export function useInventoriesQuery() {
  const query = useQuery({
    queryKey: ["inventories", "all"],
    queryFn: () => InventoryService.getAllInventories()
  });

  return {
    inventories: query.data ?? [],
    loading: query.isLoading,
    error: query.error,
  };
}
