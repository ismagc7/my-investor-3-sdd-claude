import { useState, useEffect } from 'react';
import { useBookings } from './useBookings';
import { createBooking, updateBooking, deleteBooking } from './bookingsApi';
import { getLaunches } from '../launches/launchesApi';
import type { Booking, BookingRequest, BookingStatus } from '../../shared/types/booking';
import type { Launch } from '../../shared/types/launch';
import './BookingList.css';

type FormState = {
  launchId: string;
  passengerName: string;
  passengerEmail: string;
  status: BookingStatus;
};

const EMPTY_FORM: FormState = {
  launchId: '',
  passengerName: '',
  passengerEmail: '',
  status: 'CONFIRMED',
};

function bookingToForm(booking: Booking): FormState {
  return {
    launchId: String(booking.launchId),
    passengerName: booking.passengerName,
    passengerEmail: booking.passengerEmail,
    status: booking.status,
  };
}

function formToRequest(form: FormState): BookingRequest {
  return {
    launchId: Number(form.launchId),
    passengerName: form.passengerName,
    passengerEmail: form.passengerEmail,
    status: form.status,
  };
}

export function BookingList() {
  const { data: bookings, error, isLoading, refresh } = useBookings();
  const [launches, setLaunches] = useState<Launch[]>([]);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [confirmDeleteId, setConfirmDeleteId] = useState<number | null>(null);
  const [formError, setFormError] = useState<string | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    getLaunches().then(setLaunches).catch(() => {});
  }, []);

  function openCreate() {
    setEditingId(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setShowForm(true);
  }

  function openEdit(booking: Booking) {
    setEditingId(booking.id);
    setForm(bookingToForm(booking));
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
        await updateBooking(editingId, formToRequest(form));
      } else {
        await createBooking(formToRequest(form));
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
      await deleteBooking(id);
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
      <section className="booking-section" aria-busy="true">
        <p className="booking-loading" data-testid="bookings-loading">
          <span className="booking-spinner" aria-hidden="true" />
          Loading bookings…
        </p>
      </section>
    );
  }

  if (error) {
    return (
      <section className="booking-section">
        <p className="booking-error" data-testid="bookings-error" role="alert">
          Failed to load bookings — {error.message}
        </p>
      </section>
    );
  }

  return (
    <section className="booking-section">
      <header className="booking-header">
        <h2 className="booking-title">Bookings</h2>
        <button
          className="booking-btn booking-btn--primary"
          onClick={openCreate}
          data-testid="add-booking-btn"
        >
          + New Booking
        </button>
      </header>

      {showForm && (
        <form className="booking-form" onSubmit={handleSubmit} data-testid="booking-form">
          <h3 className="booking-form-title">
            {editingId !== null ? 'Edit Booking' : 'New Booking'}
          </h3>

          <div className="booking-form-grid">
            <label className="booking-form-field">
              <span>Launch</span>
              <select
                value={form.launchId}
                onChange={(e) => handleFieldChange('launchId', e.target.value)}
                required
                data-testid="field-launch"
              >
                <option value="">Select a launch</option>
                {launches.map((l) => (
                  <option key={l.id} value={String(l.id)}>
                    {l.rocketName} — {l.date}
                  </option>
                ))}
              </select>
            </label>

            <label className="booking-form-field">
              <span>Passenger Name</span>
              <input
                type="text"
                value={form.passengerName}
                onChange={(e) => handleFieldChange('passengerName', e.target.value)}
                required
                data-testid="field-passenger-name"
              />
            </label>

            <label className="booking-form-field">
              <span>Passenger Email</span>
              <input
                type="email"
                value={form.passengerEmail}
                onChange={(e) => handleFieldChange('passengerEmail', e.target.value)}
                required
                data-testid="field-passenger-email"
              />
            </label>

            <label className="booking-form-field">
              <span>Status</span>
              <select
                value={form.status}
                onChange={(e) => handleFieldChange('status', e.target.value as BookingStatus)}
                data-testid="field-status"
              >
                <option value="CONFIRMED">Confirmed</option>
                <option value="CANCELLED">Cancelled</option>
                <option value="PAYED">Payed</option>
              </select>
            </label>
          </div>

          {formError && (
            <p className="booking-form-error" role="alert" data-testid="form-error">
              {formError}
            </p>
          )}

          <div className="booking-form-actions">
            <button
              type="button"
              className="booking-btn booking-btn--ghost"
              onClick={cancelForm}
              disabled={isSaving}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="booking-btn booking-btn--primary"
              disabled={isSaving}
              data-testid="submit-btn"
            >
              {isSaving ? 'Saving…' : editingId !== null ? 'Save Changes' : 'Add Booking'}
            </button>
          </div>
        </form>
      )}

      {bookings && bookings.length === 0 ? (
        <p className="booking-empty" data-testid="bookings-empty">
          No bookings yet. Add one to get started.
        </p>
      ) : (
        <div className="booking-table-wrapper">
          <table className="booking-table" data-testid="bookings-table">
            <thead>
              <tr>
                <th>Passenger</th>
                <th>Email</th>
                <th>Launch</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {bookings?.map((booking) => (
                <tr key={booking.id} data-testid={`booking-row-${booking.id}`}>
                  <td className="booking-passenger">{booking.passengerName}</td>
                  <td>{booking.passengerEmail}</td>
                  <td>
                    {booking.launchRocketName} — {booking.launchDate}
                  </td>
                  <td>
                    <span
                      className={`booking-status booking-status--${booking.status.toLowerCase()}`}
                    >
                      {booking.status}
                    </span>
                  </td>
                  <td className="booking-actions">
                    {confirmDeleteId === booking.id ? (
                      <span className="booking-confirm">
                        <span>Delete?</span>
                        <button
                          className="booking-btn booking-btn--danger"
                          onClick={() => handleDelete(booking.id)}
                          data-testid={`confirm-delete-${booking.id}`}
                        >
                          Yes
                        </button>
                        <button
                          className="booking-btn booking-btn--ghost"
                          onClick={() => setConfirmDeleteId(null)}
                        >
                          No
                        </button>
                      </span>
                    ) : (
                      <>
                        <button
                          className="booking-btn booking-btn--ghost"
                          onClick={() => openEdit(booking)}
                          data-testid={`edit-btn-${booking.id}`}
                        >
                          Edit
                        </button>
                        <button
                          className="booking-btn booking-btn--danger"
                          onClick={() => setConfirmDeleteId(booking.id)}
                          data-testid={`delete-btn-${booking.id}`}
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
