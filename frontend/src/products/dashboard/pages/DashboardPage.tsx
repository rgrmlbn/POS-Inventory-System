import { useDashboardQuery } from "../hooks/useDashboardQuery";

export function DashboardPage() {
  const { dashboard, loading, error } = useDashboardQuery();

  return (
    <div>
      <h1>Dashboard</h1>
      {loading && <p>Loading product count...</p>}
      {error && <p>Failed to load product count.</p>}
      {dashboard && <p>Total Product Count: {dashboard.productCount}</p>}
      {dashboard && (
        <p>Total Product Quantity: {dashboard.inventoryProductQuantity}</p>
      )}
      {dashboard && (
        <p>Out of Stock Product Count: {dashboard.outOfStockProductCount}</p>
      )}
      {dashboard && (
        <p>
          Total Inventory Value:{" "}
          {dashboard.inventoryValue.toLocaleString(undefined, {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
          })}
        </p>
      )}
      {dashboard && (
        <p>
          Total Purchase Cost:{" "}
          {dashboard.purchaseTotalCost.toLocaleString(undefined, {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
          })}
        </p>
      )}
      {dashboard && (
        <p>
          Total Sale Amount:{" "}
          {dashboard.saleTotalAmount.toLocaleString(undefined, {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
          })}
        </p>
      )}
    </div>
  );
}
