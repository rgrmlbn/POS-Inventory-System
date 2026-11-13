import type { Inventory } from "../types/Inventory";
import { Link } from "react-router-dom";

interface InventoryListProps {
  inventories: Inventory[];
  loading: boolean;
  error: unknown;
}

export function InventoryList({
  inventories,
  loading,
  error,
}: InventoryListProps) {
  if (loading) return <p>Loading...</p>;
  if (error) return <p>Failed to load inventory.</p>;
  if (inventories.length === 0) return <p>No inventory found.</p>;

  return (
    <>
      {inventories.map((inventory) => (
        <div key={inventory.id}>
          <h2>{inventory.productName}</h2>
          <h2>{inventory.quantity}</h2>
          <Link to={`/inventories/${inventory.id}`}>View details</Link>
        </div>
      ))}
    </>
  );
}
