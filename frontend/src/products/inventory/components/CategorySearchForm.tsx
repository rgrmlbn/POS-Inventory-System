import { useState } from "react";

interface CategorySearchFormProps {
  onSearch: (categoryName: string) => void;
  onClear: () => void;
}

export function CategorySearchForm({ onSearch, onClear, }: CategorySearchFormProps) {
  const [categoryName, setCategoryName] = useState("");

  return (
    <form
      onSubmit={(event) => {
        event.preventDefault();
        onSearch(categoryName.trim());
      }}
      role="search"
    >
      <input
        type="text" // use "search" if you want the browser's built-in "x" clear button
        placeholder="Search by category name..."
        value={categoryName}
        onChange={(event) => setCategoryName(event.target.value)}
      />
      <button type="submit">Search</button>
      <button
        type="button"
        onClick={() => {
          setCategoryName("");
          onClear();
        }}
      >
        Clear
      </button>
    </form>
  );
}
