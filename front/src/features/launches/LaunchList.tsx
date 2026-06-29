import { useState, useEffect } from 'react';
import { useLaunches } from './useLaunches';
import { createLaunch, updateLaunch, deleteLaunch } from './launchesApi';
import { getRockets } from '../rockets/rocketsApi';
import type { Launch, LaunchRequest, LaunchStatus } from '../../shared/types/launch';
import type { Rocket } from '../../shared/types/rocket';
import './LaunchList.css';

type FormState = {
  rocketId: string;
  date: string;
  pricePerSeat: string;
  status: LaunchStatus;
};

const EMPTY_FORM: FormState = {
  rocketId: '',
  date: '',
  pricePerSeat: '',
  status: 'CREATED',
};

function launchToForm(launch: Launch): FormState {
  return {
    rocketId: String(launch.rocketId),
    date: launch.date,
    pricePerSeat: String(launch.pricePerSeat),
    status: launch.status,
  };
}

function formToRequest(form: FormState): LaunchRequest {
  return {
    rocketId: Number(form.rocketId),
    date: form.date,
    pricePerSeat: Number(form.pricePerSeat),
    status: form.status,
  };
}

export function LaunchList() {
  const { data: launches, error, isLoading, refresh } = useLaunches();
  const [rockets, setRockets] = useState<Rocket[]>([]);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [confirmDeleteId, setConfirmDeleteId] = useState<number | null>(null);
  const [formError, setFormError] = useState<string | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    getRockets().then(setRockets).catch(() => {});
  }, []);

  function openCreate() {
    setEditingId(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setShowForm(true);
  }

  function openEdit(launch: Launch) {
    setEditingId(launch.id);
    setForm(launchToForm(launch));
    setFormError(null);
    setShowForm(true);
  }

  function cancelForm() {
    setShowForm(false);
    setEditingId(null);
    setFormError(null);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setFormError(null);
    setIsSaving(true);
    try {
      if (editingId !== null) {
        await updateLaunch(editingId, formToRequest(form));
      } else {
        await createLaunch(formToRequest(form));
      }
      setShowForm(false);
      setEditingId(null);
      refresh();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : 'Operation failed');
    } finally {
      setIsSaving(false);
    }
  }

  async function handleDelete(id: number) {
    try {
      await deleteLaunch(id);
      setConfirmDeleteId(null);
      refresh();
    } catch {
      setConfirmDeleteId(null);
    }
  }

  function handleFieldChange(field: keyof FormState, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  if (isLoading) {
    return (
      <section className="launch-section" aria-busy="true">
        <p className="launch-loading" data-testid="launches-loading">
          <span className="launch-spinner" aria-hidden="true" />
          Loading launches…
        </p>
      </section>
    );
  }

  if (error) {
    return (
      <section className="launch-section">
        <p className="launch-error" data-testid="launches-error" role="alert">
          Failed to load launches — {error.message}
        </p>
      </section>
    );
  }

  return (
    <section className="launch-section">
      <header className="launch-header">
        <h2 className="launch-title">Launches</h2>
        <button
          className="launch-btn launch-btn--primary"
          onClick={openCreate}
          data-testid="add-launch-btn"
        >
          + New Launch
        </button>
      </header>

      {showForm && (
        <form className="launch-form" onSubmit={handleSubmit} data-testid="launch-form">
          <h3 className="launch-form-title">
            {editingId !== null ? 'Edit Launch' : 'New Launch'}
          </h3>

          <div className="launch-form-grid">
            <label className="launch-form-field">
              <span>Rocket</span>
              <select
                value={form.rocketId}
                onChange={(e) => handleFieldChange('rocketId', e.target.value)}
                required
                data-testid="field-rocket"
              >
                <option value="">Select a rocket</option>
                {rockets.map((r) => (
                  <option key={r.id} value={String(r.id)}>
                    {r.name}
                  </option>
                ))}
              </select>
            </label>

            <label className="launch-form-field">
              <span>Date</span>
              <input
                type="date"
                value={form.date}
                onChange={(e) => handleFieldChange('date', e.target.value)}
                required
                data-testid="field-date"
              />
            </label>

            <label className="launch-form-field">
              <span>Price per Seat ($)</span>
              <input
                type="number"
                min="0.01"
                step="0.01"
                value={form.pricePerSeat}
                onChange={(e) => handleFieldChange('pricePerSeat', e.target.value)}
                required
                data-testid="field-price"
              />
            </label>

            <label className="launch-form-field">
              <span>Status</span>
              <select
                value={form.status}
                onChange={(e) => handleFieldChange('status', e.target.value as LaunchStatus)}
                data-testid="field-status"
              >
                <option value="CREATED">Created</option>
                <option value="CONFIRMED">Confirmed</option>
                <option value="CANCELLED">Cancelled</option>
                <option value="COMPLETED">Completed</option>
              </select>
            </label>
          </div>

          {formError && (
            <p className="launch-form-error" role="alert" data-testid="form-error">
              {formError}
            </p>
          )}

          <div className="launch-form-actions">
            <button
              type="button"
              className="launch-btn launch-btn--ghost"
              onClick={cancelForm}
              disabled={isSaving}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="launch-btn launch-btn--primary"
              disabled={isSaving}
              data-testid="submit-btn"
            >
              {isSaving ? 'Saving…' : editingId !== null ? 'Save Changes' : 'Add Launch'}
            </button>
          </div>
        </form>
      )}

      {launches && launches.length === 0 ? (
        <p className="launch-empty" data-testid="launches-empty">
          No launches scheduled yet. Add one to get started.
        </p>
      ) : (
        <div className="launch-table-wrapper">
          <table className="launch-table" data-testid="launches-table">
            <thead>
              <tr>
                <th>Rocket</th>
                <th>Date</th>
                <th>Price per Seat</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {launches?.map((launch) => (
                <tr key={launch.id} data-testid={`launch-row-${launch.id}`}>
                  <td className="launch-rocket">{launch.rocketName}</td>
                  <td>{launch.date}</td>
                  <td>${launch.pricePerSeat.toLocaleString()}</td>
                  <td>
                    <span
                      className={`launch-status launch-status--${launch.status.toLowerCase()}`}
                    >
                      {launch.status}
                    </span>
                  </td>
                  <td className="launch-actions">
                    {confirmDeleteId === launch.id ? (
                      <span className="launch-confirm">
                        <span>Delete?</span>
                        <button
                          className="launch-btn launch-btn--danger"
                          onClick={() => handleDelete(launch.id)}
                          data-testid={`confirm-delete-${launch.id}`}
                        >
                          Yes
                        </button>
                        <button
                          className="launch-btn launch-btn--ghost"
                          onClick={() => setConfirmDeleteId(null)}
                        >
                          No
                        </button>
                      </span>
                    ) : (
                      <>
                        <button
                          className="launch-btn launch-btn--ghost"
                          onClick={() => openEdit(launch)}
                          data-testid={`edit-btn-${launch.id}`}
                        >
                          Edit
                        </button>
                        <button
                          className="launch-btn launch-btn--danger"
                          onClick={() => setConfirmDeleteId(launch.id)}
                          data-testid={`delete-btn-${launch.id}`}
                        >
                          Delete
                        </button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
