import { Routes, Route } from "react-router-dom";
import { ProductPage } from "../products/products/pages/ProductPage";
import { ProductDetailPage } from "../products/products/pages/ProductDetailPage";
import { CategoryPage } from "../products/category/pages/CategoryPage";
import { InventoryPage } from "../products/inventory/pages/InventoryPage";
import { InventoryDetailPage } from "../products/inventory/pages/InventoryDetailPage";
import { SalePage } from "../products/sales/pages/SalePage";
import { SaleDetailPage } from "../products/sales/pages/SaleDetailPage";

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/products" element={<ProductPage />} />

      <Route path="/products/:id" element={<ProductDetailPage />} />

      <Route path="/categories" element={<CategoryPage />} />

      <Route path="/inventories" element={<InventoryPage />} />
      <Route path="/inventories/:id" element={<InventoryDetailPage />} />

      <Route path="/sales" element={<SalePage />} />
      <Route path="/sales/:id" element={<SaleDetailPage />} />
      <Route path="*" element={<p>Page not found.</p>} />
    </Routes>
  );
}
