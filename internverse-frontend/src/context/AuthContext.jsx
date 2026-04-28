import React, { createContext, useContext, useMemo, useState, useEffect } from 'react';

const AuthContext = createContext(null);

const STORAGE_KEY = 'internverse_auth';

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(() => {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  });

  useEffect(() => {
    if (auth) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(auth));
    } else {
      localStorage.removeItem(STORAGE_KEY);
    }
  }, [auth]);

  const value = useMemo(
    () => ({
      user: auth,
      token: auth?.token ?? null,
      role: auth?.role ?? null,
      login: (payload) => setAuth(payload),
      logout: () => setAuth(null),
      isAuthenticated: !!auth?.token,
    }),
    [auth]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
