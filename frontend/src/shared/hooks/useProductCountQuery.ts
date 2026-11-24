import { useQuery } from "@tanstack/react-query";
import { DashboardService } from "../services/DashboardService";

export function useProductCountQuery() {
  return useQuery({
    queryKey: ["dashboard", "product-count"],
    queryFn: DashboardService.getProductCount,
  });
}
