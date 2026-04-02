import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { getCart } from '../api/cart';
import { useAuth } from './AuthContext';

const CartContext = createContext(null);

const GUEST_CART_KEY = 'ghost_guest_cart';

// --- Guest Cart helpers (localStorage) ---
function getGuestCartItems() {
  try {
    return JSON.parse(localStorage.getItem(GUEST_CART_KEY)) || [];
  } catch { return []; }
}

function saveGuestCartItems(items) {
  localStorage.setItem(GUEST_CART_KEY, JSON.stringify(items));
}

function buildGuestCart(items) {
  const totalAmount = items.reduce((sum, i) => sum + (i.discountPrice || i.price) * i.quantity, 0);
  const totalCount = items.reduce((sum, i) => sum + i.quantity, 0);
  return { items, totalAmount, totalCount };
}

export function CartProvider({ children }) {
  const { user } = useAuth();
  const [cart, setCart] = useState({ items: [], totalAmount: 0, totalCount: 0 });

  // --- Fetch cart: server (logged in) or localStorage (guest) ---
  const fetchCart = useCallback(async () => {
    if (user) {
      try {
        const res = await getCart();
        setCart(res.data);
      } catch {
        setCart({ items: [], totalAmount: 0, totalCount: 0 });
      }
    } else {
      setCart(buildGuestCart(getGuestCartItems()));
    }
  }, [user]);

  useEffect(() => {
    fetchCart();
  }, [fetchCart]);

  // --- Guest cart: add item ---
  const addGuestItem = useCallback((product, quantity, selectedSize, selectedColor) => {
    const items = getGuestCartItems();
    const existing = items.find(
      (i) => i.productId === product.id && i.selectedSize === selectedSize && i.selectedColor === selectedColor
    );
    if (existing) {
      existing.quantity += quantity;
    } else {
      items.push({
        id: Date.now(), // unique key for React
        productId: product.id,
        productName: product.name,
        productImage: product.mainImage,
        price: product.price,
        discountPrice: product.discountPrice,
        quantity,
        selectedSize,
        selectedColor,
        stock: product.stock,
      });
    }
    saveGuestCartItems(items);
    setCart(buildGuestCart(items));
  }, []);

  // --- Guest cart: update quantity ---
  const updateGuestItem = useCallback((itemId, quantity) => {
    const items = getGuestCartItems().map((i) =>
      i.id === itemId ? { ...i, quantity: Math.max(1, quantity) } : i
    );
    saveGuestCartItems(items);
    setCart(buildGuestCart(items));
  }, []);

  // --- Guest cart: remove item ---
  const removeGuestItem = useCallback((itemId) => {
    const items = getGuestCartItems().filter((i) => i.id !== itemId);
    saveGuestCartItems(items);
    setCart(buildGuestCart(items));
  }, []);

  // --- Guest cart: clear ---
  const clearGuestCart = useCallback(() => {
    localStorage.removeItem(GUEST_CART_KEY);
    setCart({ items: [], totalAmount: 0, totalCount: 0 });
  }, []);

  return (
    <CartContext.Provider value={{
      cart, setCart, fetchCart,
      addGuestItem, updateGuestItem, removeGuestItem, clearGuestCart,
      isGuest: !user,
    }}>
      {children}
    </CartContext.Provider>
  );
}

export const useCart = () => useContext(CartContext);
