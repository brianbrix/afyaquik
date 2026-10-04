import { apiRequest, portalUrl } from "@afyaquik/shared";
import React, { useState } from "react";

const ForgotPasswordPage = () => {
    const [username, setUsername] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setSuccess(false);
        try {
            await apiRequest("/password-reset/request", {
                method: "POST",
                body: { username },
            });
            setSuccess(true);
        } catch {
            setError("Unable to submit your request. Please try again.");
        }
        setLoading(false);
    };

    return (
        <div className="container py-5" style={{ maxWidth: 440 }}>
            <h1 className="h3 mb-4">Reset Password</h1>
            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label htmlFor="username" className="form-label">Username</label>
                    <input
                        className="form-control"
                        id="username"
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                {error && <p className="alert alert-danger" role="alert">{error}</p>}
                {success && <p className="alert alert-success" role="status">If the account exists, a reset request has been sent to your facility administrator.</p>}
                <button type="submit" className="btn btn-primary" disabled={loading}>
                    {loading ? "Loading..." : "Request Password Reset"}
                </button>
            </form>
            <a className="d-inline-block mt-3" href={portalUrl('auth', '/login')}>Back to sign in</a>
        </div>
    );
};

export default ForgotPasswordPage;
