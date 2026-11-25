import { useDashboardQuery } from "../hooks/useDashboardQuery";

export function DashboardPage() {
  const { dashboard, loading, error } = useDashboardQuery();

  return (
    <div>
      <h1>Dashboard</h1>
      {loading && <p>Loading product count...</p>}
      {error && <p>Failed to load product count.</p>}
      {dashboard && (
        <p>Total Product Count: {dashboard.totalProductCount}</p>
      )}
      {dashboard && (
        <p>Total Product Quantity: {dashboard.totalProductQuantity}</p>
      )}
      {dashboard && (
        <p>
          Inventory value:{" "}
          {dashboard.totalInventoryValue.toLocaleString(undefined, {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
          })}
        </p>
      )}
      {dashboard && (
        <p>
          Total Purchase Cost:{" "}
          {dashboard.totalPurchaseCost.toLocaleString(undefined, {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
          })}
        </p>
      )}
    </div>
  );
}
