import { useState } from "react";

interface ProductSearchFormProps {
  onSearch: (productName: string) => void;
  onClear: () => void;
}

export function ProductSearchForm({ onSearch, onClear, }: ProductSearchFormProps) {
  const [productName, setProductName] = useState("");

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
        onChange={(event) => setProductName(event.target.value)}
      />
      <button type="submit">Search</button>
      <button
        type="button"
        onClick={() => {
          setProductName("");
          onClear();
        }}
      >
        Clear
      </button>
    </form>
  );
}
