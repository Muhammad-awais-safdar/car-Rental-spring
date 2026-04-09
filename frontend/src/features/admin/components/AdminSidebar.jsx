import { Link, useLocation } from "react-router-dom";
import { useState } from "react";
import { useAuth } from "../../auth/context/AuthContext";

export default function AdminSidebar({ isOpen, onClose }) {
  const location = useLocation();
  const { user } = useAuth();
  const [collapsed, setCollapsed] = useState(false);

  const menuItems = [
    { icon: "📊", label: "Dashboard", path: "/admin/dashboard" },
    { icon: "👥", label: "Users", path: "/admin/users" },
    { icon: "🚗", label: "Listings", path: "/admin/listings" },
    { icon: "📅", label: "Bookings", path: "/admin/bookings" },
    { icon: "⭐", label: "Reviews", path: "/admin/reviews" },
    { icon: "🏷️", label: "Categories", path: "/admin/categories" },
    { icon: "📄", label: "Reports", path: "/admin/reports" },
    { icon: "📈", label: "Analytics", path: "/admin/analytics" },
    { icon: "📋", label: "Activity Logs", path: "/admin/activity-logs" },
    { icon: "⚙️", label: "Settings", path: "/admin/settings" },
  ];

  const isActive = (path) => location.pathname === path;

  return (
    <>
      {/* Mobile Overlay */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
          onClick={onClose}
        />
      )}

      {/* Sidebar */}
      <aside
        className={`
          fixed top-0 left-0 h-full bg-slate-900 text-white z-50
          transform transition-all duration-300 ease-in-out
          ${isOpen ? "translate-x-0" : "-translate-x-full lg:translate-x-0"}
          ${collapsed ? "lg:w-20" : "lg:w-64"}
          w-64
        `}
      >
        {/* Header */}
        <div className="h-20 flex items-center justify-between px-4 border-b border-slate-700">
          {!collapsed && (
            <Link to="/" className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-gradient-to-br from-red-600 to-red-700 rounded-lg flex items-center justify-center">
                <svg
                  className="w-6 h-6 text-white"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M13 10V3L4 14h7v7l9-11h-7z"
                  />
                </svg>
              </div>
              <div>
                <div className="text-lg font-bold">CarMarket</div>
                <div className="text-xs text-slate-400">Admin Panel</div>
              </div>
            </Link>
          )}

          {/* Toggle Button - Desktop */}
          <button
            onClick={() => setCollapsed(!collapsed)}
            className="hidden lg:block p-2 hover:bg-slate-800 rounded-lg transition-colors"
          >
            <svg
              className={`w-5 h-5 transition-transform ${
                collapsed ? "rotate-180" : ""
              }`}
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M11 19l-7-7 7-7m8 14l-7-7 7-7"
              />
            </svg>
          </button>

          {/* Close Button - Mobile */}
          <button
            onClick={onClose}
            className="lg:hidden p-2 hover:bg-slate-800 rounded-lg transition-colors"
          >
            <svg
              className="w-6 h-6"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>

        {/* Navigation */}
        <nav className="flex-1 overflow-y-auto py-4 px-2">
          <div className="space-y-1">
            {menuItems.map((item) => (
              <Link
                key={item.path}
                to={item.path}
                onClick={() => window.innerWidth < 1024 && onClose()}
                className={`
                  flex items-center space-x-3 px-3 py-3 rounded-lg
                  transition-all duration-200
                  ${
                    isActive(item.path)
                      ? "bg-red-600 text-white shadow-lg"
                      : "text-slate-300 hover:bg-slate-800 hover:text-white"
                  }
                  ${collapsed ? "justify-center" : ""}
                `}
                title={collapsed ? item.label : ""}
              >
                <span className="text-xl">{item.icon}</span>
                {!collapsed && (
                  <span className="font-medium">{item.label}</span>
                )}
              </Link>
            ))}
          </div>
        </nav>

        {/* User Info */}
        {!collapsed && user && (
          <div className="p-4 border-t border-slate-700">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center text-white font-bold">
                {user.firstName?.charAt(0) || "A"}
              </div>
              <div className="flex-1 min-w-0">
                <div className="text-sm font-medium truncate">
                  {user.firstName} {user.lastName}
                </div>
                <div className="text-xs text-slate-400 truncate">
                  {user.email}
                </div>
              </div>
            </div>
          </div>
        )}
      </aside>
    </>
  );
}
