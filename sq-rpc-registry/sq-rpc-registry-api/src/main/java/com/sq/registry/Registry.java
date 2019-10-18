package com.sq.registry;


import com.sq.common.Node;
import com.sq.common.URL;
import com.sq.registry.support.AbstractRegistry;

/**
 * Registry. (SPI, Prototype, ThreadSafe)
 *
 * @see RegistryFactory#getRegistry(URL)
 * @see AbstractRegistry
 */
public interface Registry extends Node, RegistryService {
}