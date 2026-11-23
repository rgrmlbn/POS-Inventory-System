import { Routes, Route } from "react-router-dom";

import { ProductPage } from "../products/product/pages/ProductPage";
import { ProductDetailPage } from "../products/product/pages/ProductDetailPage";
import { CategoryPage } from "../products/category/pages/CategoryPage";
import { InventoryPage } from "../products/inventory/pages/InventoryPage";
import { InventoryDetailPage } from "../products/inventory/pages/InventoryDetailPage";
import { SalePage } from "../products/sale/pages/SalePage";
import { SaleDetailPage } from "../products/sale/pages/SaleDetailPage";
import { PurchasePage } from "../products/purchase/pages/PurchasePage";
import { PurchaseDetailPage } from "../products/purchase/pages/PurchaseDetailPage";
import { Layout } from "../shared/components/Layout";
import { Dashboard } from "../shared/components/dashboard";

export function AppRoutes() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/dashboard" element={<Dashboard />} />

        <Route path="/products" element={<ProductPage />} />

        <Route path="/products/:id" element={<ProductDetailPage />} />

        <Route path="/categories" element={<CategoryPage />} />

        <Route path="/inventories" element={<InventoryPage />} />
        <Route path="/inventories/:id" element={<InventoryDetailPage />} />

        <Route path="/sales" element={<SalePage />} />
        <Route path="/sales/:id" element={<SaleDetailPage />} />

        <Route path="/purchases" element={<PurchasePage />} />
        <Route path="/purchases/:id" element={<PurchaseDetailPage />} />

        <Route path="*" element={<p>Page not found.</p>} />
      </Route>
    </Routes>
  );
}
