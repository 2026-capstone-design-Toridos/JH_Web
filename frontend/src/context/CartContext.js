import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { getCart } from '../api/cart';
import { useAuth } from './AuthContext';

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const { user } = useAuth();
  const [cart, setCart] = useState({ items: [], totalAmount: 0, totalCount: 0 });

  const fetchCart = useCallback(async () => {
    if (!user) {
      setCart({ items: [], totalAmount: 0, totalCount: 0 });
      return;
    }
    try {
      const res = await getCart();
      setCart(res.data);
    } catch {
      setCart({ items: [], totalAmount: 0, totalCount: 0 });
    }
  }, [user]);

  useEffect(() => {
    fetchCart();
  }, [fetchCart]);

  return (
    <CartContext.Provider value={{ cart, setCart, fetchCart }}>
      {children}
    </CartContext.Provider>
  );
}

export const useCart = () => useContext(CartContext);
