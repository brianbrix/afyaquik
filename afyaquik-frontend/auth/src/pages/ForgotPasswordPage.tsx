import { apiRequest } from "@afyaquik/shared";
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
            setError("Failed to request password reset. Please check your username.");
        }
        setLoading(false);
    };

    return (
        <div>
            <h1>Reset Password</h1>
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="username">Username</label>
                    <input
                        id="username"
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                {error && <p style={{ color: "red" }}>{error}</p>}
                {success && <p style={{ color: "green" }}>Check your email for reset link!</p>}
                <button type="submit" disabled={loading}>
                    {loading ? "Loading..." : "Request Password Reset"}
                </button>
            </form>
        </div>
    );
};

export default ForgotPasswordPage;
