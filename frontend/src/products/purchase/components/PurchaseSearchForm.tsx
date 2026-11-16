import { useState } from "react";

interface PurchaseSearchFormProps {
  onSearch: (productName: string) => void;
  onClear: () => void;
}

export function PurchaseSearchForm({ onSearch, onClear, }: PurchaseSearchFormProps) {
  const [productName, setproductName] = useState("");

  return (
    <form
      onSubmit={(event) => {
        event.preventDefault();
        onSearch(productName.trim());
      }}
      role="search"
    >
      <input
        type="text" // use "search" if you want the browser's built-in "x" clear button
        placeholder="Search by product name..."
        value={productName}
        onChange={(event) => setproductName(event.target.value)}
      />
      <button type="submit">Search</button>
      <button
        type="button"
        onClick={() => {
          setproductName("");
          onClear();
        }}
      >
        Clear
      </button>
    </form>
  );
}
