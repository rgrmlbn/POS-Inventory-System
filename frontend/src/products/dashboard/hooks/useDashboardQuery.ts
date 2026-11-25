import { useQuery } from "@tanstack/react-query";
import { DashboardService } from "../services/DashboardService";

export function useDashboardQuery() {
  const query = useQuery({
    queryKey: ["dashboard", "all"],
    queryFn: DashboardService.getDashboardData,
  });

  return {
    dashboard: query.data,
    loading: query.isLoading,
    error: query.error,
  };
}
