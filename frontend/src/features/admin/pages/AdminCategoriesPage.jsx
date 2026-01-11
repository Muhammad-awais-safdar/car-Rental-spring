import { useState, useEffect } from "react";
import { categoryService } from "../services/adminService";
import LoadingSpinner from "../../../shared/components/LoadingSpinner";

export default function AdminCategoriesPage() {
  const [makes, setMakes] = useState([]);
  const [selectedMake, setSelectedMake] = useState(null);
  const [models, setModels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showMakeModal, setShowMakeModal] = useState(false);
  const [showModelModal, setShowModelModal] = useState(false);
  const [makeName, setMakeName] = useState("");
  const [modelName, setModelName] = useState("");

  useEffect(() => {
    fetchMakes();
  }, []);

  const fetchMakes = async () => {
    try {
      const response = await categoryService.getAllMakes();
      setMakes(response.data);
    } catch (error) {
      console.error("Error fetching makes:", error);
    } finally {
      setLoading(false);
    }
  };

  const fetchModels = async (makeId) => {
    try {
      const response = await categoryService.getMakeWithModels(makeId);
      setModels(response.data.models);
      setSelectedMake(response.data);
    } catch (error) {
      console.error("Error fetching models:", error);
    }
  };

  const handleCreateMake = async () => {
    if (!makeName.trim()) {
      alert("Please enter a make name");
      return;
    }

    try {
      await categoryService.createMake(makeName, null);
      setShowMakeModal(false);
      setMakeName("");
      fetchMakes();
      alert("Make created successfully");
    } catch (error) {
      alert(error.response?.data?.statusMessage || "Failed to create make");
    }
  };

  const handleCreateModel = async () => {
    if (!modelName.trim() || !selectedMake) {
      alert("Please enter a model name");
      return;
    }

    try {
      await categoryService.createModel(selectedMake.id, modelName);
      setShowModelModal(false);
      setModelName("");
      fetchModels(selectedMake.id);
      alert("Model created successfully");
    } catch (error) {
      alert(error.response?.data?.statusMessage || "Failed to create model");
    }
  };

  const handleDeleteMake = async (makeId) => {
    if (
      !confirm(
        "Are you sure you want to delete this make? All associated models will also be deleted."
      )
    ) {
      return;
    }

    try {
      await categoryService.deleteMake(makeId);
      fetchMakes();
      if (selectedMake?.id === makeId) {
        setSelectedMake(null);
        setModels([]);
      }
      alert("Make deleted successfully");
    } catch (error) {
      alert(error.response?.data?.statusMessage || "Failed to delete make");
    }
  };

  const handleDeleteModel = async (modelId) => {
    if (!confirm("Are you sure you want to delete this model?")) {
      return;
    }

    try {
      await categoryService.deleteModel(modelId);
      fetchModels(selectedMake.id);
      alert("Model deleted successfully");
    } catch (error) {
      alert(error.response?.data?.statusMessage || "Failed to delete model");
    }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-3xl font-bold text-gray-900">
            Category Management
          </h1>
          <button
            onClick={() => setShowMakeModal(true)}
            className="px-6 py-3 bg-[#DC2626] text-white rounded-xl font-semibold hover:bg-[#B91C1C] transition-colors"
          >
            + Add Make
          </button>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Makes List */}
          <div className="bg-white rounded-xl shadow-lg p-6">
            <h2 className="text-xl font-bold text-gray-900 mb-4">
              Car Makes ({makes.length})
            </h2>
            <div className="space-y-2 max-h-96 overflow-y-auto">
              {makes.map((make) => (
                <div
                  key={make.id}
                  onClick={() => fetchModels(make.id)}
                  className={`p-4 border-2 rounded-lg cursor-pointer transition-all ${
                    selectedMake?.id === make.id
                      ? "border-[#DC2626] bg-red-50"
                      : "border-gray-200 hover:border-gray-300"
                  }`}
                >
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="font-semibold text-gray-900">{make.name}</p>
                      <p className="text-sm text-gray-600">
                        {make.modelCount} models
                      </p>
                    </div>
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        handleDeleteMake(make.id);
                      }}
                      className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                    >
                      <svg
                        className="w-5 h-5"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth="2"
                          d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                        />
                      </svg>
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Models List */}
          <div className="bg-white rounded-xl shadow-lg p-6">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-bold text-gray-900">
                {selectedMake ? `${selectedMake.name} Models` : "Select a Make"}
              </h2>
              {selectedMake && (
                <button
                  onClick={() => setShowModelModal(true)}
                  className="px-4 py-2 bg-[#DC2626] text-white rounded-lg font-semibold hover:bg-[#B91C1C] transition-colors"
                >
                  + Add Model
                </button>
              )}
            </div>

            {selectedMake ? (
              <div className="space-y-2 max-h-96 overflow-y-auto">
                {models.length === 0 ? (
                  <p className="text-center text-gray-500 py-8">
                    No models yet
                  </p>
                ) : (
                  models.map((model) => (
                    <div
                      key={model.id}
                      className="p-4 border-2 border-gray-200 rounded-lg hover:border-gray-300 transition-all"
                    >
                      <div className="flex items-center justify-between">
                        <p className="font-semibold text-gray-900">
                          {model.name}
                        </p>
                        <button
                          onClick={() => handleDeleteModel(model.id)}
                          className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                        >
                          <svg
                            className="w-5 h-5"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth="2"
                              d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                            />
                          </svg>
                        </button>
                      </div>
                    </div>
                  ))
                )}
              </div>
            ) : (
              <div className="text-center text-gray-400 py-16">
                <svg
                  className="w-16 h-16 mx-auto mb-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.657-1.657a2 2 0 012.828 0l2.829 2.829a2 2 0 010 2.828l-8.486 8.485M7 17h.01"
                  />
                </svg>
                <p>Select a make to view its models</p>
              </div>
            )}
          </div>
        </div>

        {/* Add Make Modal */}
        {showMakeModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-xl p-6 max-w-md w-full mx-4">
              <h3 className="text-xl font-bold text-gray-900 mb-4">
                Add New Make
              </h3>
              <input
                type="text"
                value={makeName}
                onChange={(e) => setMakeName(e.target.value)}
                placeholder="Make name (e.g., Toyota)"
                className="w-full px-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#DC2626] focus:ring-4 focus:ring-red-100 transition-all outline-none"
              />
              <div className="flex space-x-3 mt-4">
                <button
                  onClick={handleCreateMake}
                  className="flex-1 px-4 py-2 bg-[#DC2626] text-white rounded-lg font-semibold hover:bg-[#B91C1C]"
                >
                  Create
                </button>
                <button
                  onClick={() => {
                    setShowMakeModal(false);
                    setMakeName("");
                  }}
                  className="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg font-semibold hover:bg-gray-300"
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Add Model Modal */}
        {showModelModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-xl p-6 max-w-md w-full mx-4">
              <h3 className="text-xl font-bold text-gray-900 mb-4">
                Add Model to {selectedMake?.name}
              </h3>
              <input
                type="text"
                value={modelName}
                onChange={(e) => setModelName(e.target.value)}
                placeholder="Model name (e.g., Camry)"
                className="w-full px-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#DC2626] focus:ring-4 focus:ring-red-100 transition-all outline-none"
              />
              <div className="flex space-x-3 mt-4">
                <button
                  onClick={handleCreateModel}
                  className="flex-1 px-4 py-2 bg-[#DC2626] text-white rounded-lg font-semibold hover:bg-[#B91C1C]"
                >
                  Create
                </button>
                <button
                  onClick={() => {
                    setShowModelModal(false);
                    setModelName("");
                  }}
                  className="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg font-semibold hover:bg-gray-300"
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
