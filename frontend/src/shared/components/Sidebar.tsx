import { NavLink } from "react-router-dom";

const menus = [
  { label: "Dashboard", path: "/dashboard" },
  { label: "Product", path: "/products" },
  { label: "Category", path: "/categories" },
  { label: "Inventory", path: "/inventories" },
  { label: "Sale", path: "/sales" },
  { label: "Purchase", path: "/purchases" },
];

export function Sidebar() {
  return (
    <aside>
      <h2>User</h2>
      <nav>
        <ul>
          {menus.map((menu) => (
            <li key={menu.path}>
              <NavLink to={menu.path}>{menu.label}</NavLink>
            </li>
          ))}
        </ul>
      </nav>
    </aside>
  );
}
