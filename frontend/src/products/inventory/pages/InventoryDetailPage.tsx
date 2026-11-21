// pages/ProductDetailPage.tsx
import { useParams, Link } from "react-router-dom";
import { useInventoryQuery } from "../hooks/get/useInventoryQuery";
import type { Inventory } from "../types/Inventory";
import { InventoryEditForm } from "../components/InventoryEditForm";
import { useState } from "react";

function InventoryItem({ inventory }: { inventory: Inventory }) {
  const [isEditing, setIsEditing] = useState(false);

  if (isEditing) {
    return (
      <InventoryEditForm
        inventory={inventory}
        onDone={() => setIsEditing(false)}
      />
    );
  } else
    return (
      <div>
        <h1>{inventory.productName}</h1>

        <h2>{inventory.categoryName}</h2>
        <h2>{inventory.sku}</h2>
        <p>
          <b>Quantity: </b>
          {inventory.quantity}
        </p>
        <p>
          <b>Reorder Level: </b>
          {inventory.reorderLevel}
        </p>
        <button type="button" onClick={() => setIsEditing(true)}>
          Edit
        </button>
      </div>
    );
}

export function InventoryDetailPage() {
  const { id } = useParams();
  const { inventory, loading, error } = useInventoryQuery(Number(id));

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Failed to load product inventory details.</p>;
  if (!inventory) return <p>Product Inventory details not found.</p>;

  return (
    <div>
      <Link to="/inventories">← Back to inventories</Link>
      <InventoryItem key={inventory.id} inventory={inventory} />
    </div>
  );
}
