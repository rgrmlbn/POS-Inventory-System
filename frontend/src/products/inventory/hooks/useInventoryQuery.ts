import { useQuery } from "@tanstack/react-query";
import { InventoryService } from "../services/InventoryService";

export function useInventoryQuery(id: number) {
  const query = useQuery({
    queryKey: ["inventory", id],
    queryFn: () => InventoryService.getInventoryById(id),
  });

  return {
    inventory: query.data,
    loading: query.isLoading,
    error: query.error,
  };
}
