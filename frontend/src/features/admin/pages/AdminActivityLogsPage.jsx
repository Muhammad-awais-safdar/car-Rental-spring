import { useState, useEffect } from "react";
import api from "../../../core/api/apiClient";
import LoadingSpinner from "../../../shared/components/LoadingSpinner";

const API_BASE = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

export default function AdminActivityLogsPage() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [filterAction, setFilterAction] = useState("");

  useEffect(() => {
    fetchLogs();
  }, [page, filterAction]);

  const fetchLogs = async () => {
    setLoading(true);
    try {
      const url = filterAction
        ? `${API_BASE}/admin/logs/action/${filterAction}?page=${page}&size=50`
        : `${API_BASE}/admin/logs?page=${page}&size=50`;

      const response = await api.get(url);
      setLogs(response.data.data.content);
      setTotalPages(response.data.data.totalPages);
    } catch (error) {
      console.error("Error fetching activity logs:", error);
    } finally {
      setLoading(false);
    }
  };

  const getActionColor = (action) => {
    const colors = {
      CREATE: "bg-green-100 text-green-800",
      UPDATE: "bg-blue-100 text-blue-800",
      DELETE: "bg-red-100 text-red-800",
      LOGIN: "bg-purple-100 text-purple-800",
      LOGOUT: "bg-gray-100 text-gray-800",
      BLOCK: "bg-red-100 text-red-800",
      UNBLOCK: "bg-green-100 text-green-800",
    };
    return colors[action] || "bg-gray-100 text-gray-800";
  };

  if (loading && logs.length === 0) return <LoadingSpinner />;

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Activity Logs</h1>

          {/* Filter */}
          <div className="flex items-center space-x-3">
            <label className="text-sm font-semibold text-gray-700">
              Filter by Action:
            </label>
            <select
              value={filterAction}
              onChange={(e) => {
                setFilterAction(e.target.value);
                setPage(0);
              }}
              className="px-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#DC2626] focus:ring-4 focus:ring-red-100 transition-all outline-none"
            >
              <option value="">All Actions</option>
              <option value="CREATE">Create</option>
              <option value="UPDATE">Update</option>
              <option value="DELETE">Delete</option>
              <option value="LOGIN">Login</option>
              <option value="LOGOUT">Logout</option>
              <option value="BLOCK">Block</option>
              <option value="UNBLOCK">Unblock</option>
            </select>
          </div>
        </div>

        {/* Logs Table */}
        <div className="bg-white rounded-xl shadow-lg overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    ID
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    User
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Action
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Entity
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Details
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    IP Address
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Timestamp
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {logs.length === 0 ? (
                  <tr>
                    <td
                      colSpan="7"
                      className="px-6 py-12 text-center text-gray-500"
                    >
                      No activity logs found
                    </td>
                  </tr>
                ) : (
                  logs.map((log) => (
                    <tr key={log.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4 text-sm text-gray-900">
                        #{log.id}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-900">
                        {log.user ? (
                          <div>
                            <p className="font-semibold">
                              {log.user.firstName} {log.user.lastName}
                            </p>
                            <p className="text-xs text-gray-500">
                              {log.user.email}
                            </p>
                          </div>
                        ) : (
                          <span className="text-gray-400">System</span>
                        )}
                      </td>
                      <td className="px-6 py-4">
                        <span
                          className={`px-3 py-1 text-xs font-semibold rounded-full ${getActionColor(
                            log.action
                          )}`}
                        >
                          {log.action}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600">
                        {log.entityType && (
                          <div>
                            <p className="font-medium">{log.entityType}</p>
                            <p className="text-xs text-gray-500">
                              ID: {log.entityId}
                            </p>
                          </div>
                        )}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600 max-w-xs truncate">
                        {log.details || "-"}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600">
                        {log.ipAddress || "-"}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600">
                        {new Date(log.createdAt).toLocaleString()}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="px-6 py-4 flex items-center justify-between border-t">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg disabled:opacity-50 hover:bg-gray-300 transition-colors"
              >
                Previous
              </button>
              <span className="text-sm text-gray-600">
                Page {page + 1} of {totalPages}
              </span>
              <button
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page >= totalPages - 1}
                className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg disabled:opacity-50 hover:bg-gray-300 transition-colors"
              >
                Next
              </button>
            </div>
          )}
        </div>

        {/* Stats Summary */}
        <div className="mt-6 bg-white rounded-xl shadow-lg p-6">
          <h3 className="text-lg font-bold text-gray-900 mb-4">
            Activity Summary
          </h3>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="text-center">
              <p className="text-2xl font-bold text-gray-900">{logs.length}</p>
              <p className="text-sm text-gray-600">Logs on this page</p>
            </div>
            <div className="text-center">
              <p className="text-2xl font-bold text-green-600">
                {logs.filter((l) => l.action === "CREATE").length}
              </p>
              <p className="text-sm text-gray-600">Create Actions</p>
            </div>
            <div className="text-center">
              <p className="text-2xl font-bold text-blue-600">
                {logs.filter((l) => l.action === "UPDATE").length}
              </p>
              <p className="text-sm text-gray-600">Update Actions</p>
            </div>
            <div className="text-center">
              <p className="text-2xl font-bold text-red-600">
                {logs.filter((l) => l.action === "DELETE").length}
              </p>
              <p className="text-sm text-gray-600">Delete Actions</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
