/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.util.inv;


import appeng.api.config.FuzzyMode;
import appeng.api.config.InsertionMode;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.Iterator;


public class AdaptorIInventory extends InventoryAdaptor
{

	private final IInventory i;
	private final boolean wrapperEnabled;
    private final boolean skipStackSizeCheck;
    public AdaptorIInventory( final IInventory s )
	{
		this.i = s;
        skipStackSizeCheck = i.getClass().toString().equals("class wanion.avaritiaddons.block.chest.infinity.TileEntityInfinityChest");
		this.wrapperEnabled = s instanceof IInventoryWrapper;
	}

	@Override
	public ItemStack removeItems( int amount, ItemStack filter, final IInventoryDestination destination )
	{
		final int s = this.i.getSizeInventory();
		ItemStack rv = null;

		for( int x = 0; x < s && amount > 0; x++ )
		{
			final ItemStack is = this.i.getStackInSlot( x );
			if( is != null && this.canRemoveStackFromSlot( x, is ) && ( filter == null || Platform.isSameItemPrecise( is, filter ) ) )
			{
				int boundAmounts = amount;
				if( boundAmounts > is.stackSize )
				{
					boundAmounts = is.stackSize;
				}
				if( destination != null && !destination.canInsert( is ) )
				{
					boundAmounts = 0;
				}

				if( boundAmounts > 0 )
				{
					if( rv == null )
					{
						rv = is.copy();
						filter = rv;
						rv.stackSize = boundAmounts;
						amount -= boundAmounts;
					}
					else
					{
						rv.stackSize += boundAmounts;
						amount -= boundAmounts;
					}

					if( is.stackSize == boundAmounts )
					{
						this.i.setInventorySlotContents( x, null );
						this.i.markDirty();
					}
					else
					{
						final ItemStack po = is.copy();
						po.stackSize -= boundAmounts;
						this.i.setInventorySlotContents( x, po );
						this.i.markDirty();
					}
				}
			}
		}

		// if ( rv != null )
		// i.markDirty();

		return rv;
	}

	@Override
	public ItemStack simulateRemove( int amount, final ItemStack filter, final IInventoryDestination destination )
	{
		final int s = this.i.getSizeInventory();
		ItemStack rv = null;

		for( int x = 0; x < s && amount > 0; x++ )
		{
			final ItemStack is = this.i.getStackInSlot( x );
			if( is != null && this.canRemoveStackFromSlot( x, is ) && ( filter == null || Platform.isSameItemPrecise( is, filter ) ) )
			{
				int boundAmount = amount;
				if( boundAmount > is.stackSize )
				{
					boundAmount = is.stackSize;
				}
				if( destination != null && !destination.canInsert( is ) )
				{
					boundAmount = 0;
				}

				if( boundAmount > 0 )
				{
					if( rv == null )
					{
						rv = is.copy();
						rv.stackSize = boundAmount;
						amount -= boundAmount;
					}
					else
					{
						rv.stackSize += boundAmount;
						amount -= boundAmount;
					}
				}
			}
		}

		return rv;
	}

	@Override
	public ItemStack removeSimilarItems( final int amount, final ItemStack filter, final FuzzyMode fuzzyMode, final IInventoryDestination destination )
	{
		final int s = this.i.getSizeInventory();
		for( int x = 0; x < s; x++ )
		{
			final ItemStack is = this.i.getStackInSlot( x );
			if( is != null && this.canRemoveStackFromSlot( x, is ) && ( filter == null || Platform.isSameItemFuzzy( is, filter, fuzzyMode ) ) )
			{
				int newAmount = amount;
				if( newAmount > is.stackSize )
				{
					newAmount = is.stackSize;
				}
				if( destination != null && !destination.canInsert( is ) )
				{
					newAmount = 0;
				}

				ItemStack rv = null;
				if( newAmount > 0 )
				{
					rv = is.copy();
					rv.stackSize = newAmount;

					if( is.stackSize == rv.stackSize )
					{
						this.i.setInventorySlotContents( x, null );
						this.i.markDirty();
					}
					else
					{
						final ItemStack po = is.copy();
						po.stackSize -= rv.stackSize;
						this.i.setInventorySlotContents( x, po );
						this.i.markDirty();
					}
				}

				if( rv != null )
				{
					// i.markDirty();
					return rv;
				}
			}
		}
		return null;
	}

	@Override
	public ItemStack simulateSimilarRemove( final int amount, final ItemStack filter, final FuzzyMode fuzzyMode, final IInventoryDestination destination )
	{
		final int s = this.i.getSizeInventory();
		for( int x = 0; x < s; x++ )
		{
			final ItemStack is = this.i.getStackInSlot( x );

			if( is != null && this.canRemoveStackFromSlot( x, is ) && ( filter == null || Platform.isSameItemFuzzy( is, filter, fuzzyMode ) ) )
			{
				int boundAmount = amount;
				if( boundAmount > is.stackSize )
				{
					boundAmount = is.stackSize;
				}
				if( destination != null && !destination.canInsert( is ) )
				{
					boundAmount = 0;
				}

				if( boundAmount > 0 )
				{
					final ItemStack rv = is.copy();
					rv.stackSize = boundAmount;
					return rv;
				}
			}
		}
		return null;
	}

	@Override
	public ItemStack addItems( final ItemStack toBeAdded )
	{
		return this.addItems( toBeAdded, true, InsertionMode.DEFAULT );
	}

	@Override
	public ItemStack addItems( ItemStack toBeAdded, InsertionMode insertionMode )
	{
		return this.addItems( toBeAdded, true, insertionMode );
	}

	@Override
	public ItemStack simulateAdd( final ItemStack toBeSimulated )
	{
		return this.addItems( toBeSimulated, false, InsertionMode.DEFAULT );
	}

	@Override
	public ItemStack simulateAdd( ItemStack toBeSimulated, InsertionMode insertionMode )
	{
		return this.addItems( toBeSimulated, false, insertionMode );
	}

	@Override
	public boolean containsItems()
	{
		final int s = this.i.getSizeInventory();
		for( int x = 0; x < s; x++ )
		{
			if( this.i.getStackInSlot( x ) != null )
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds an {@link ItemStack} to the adapted {@link IInventory}.
	 * <p>
	 * It respects the inventories stack limit, which can result in not all items added and some left ones are returned.
	 * The ItemStack next is required for inventories, which will fail on isItemValidForSlot() for stacksizes larger
	 * than the limit.
	 *
	 * @param itemsToAdd itemStack to add to the inventory
	 * @param modulate   true to modulate, false for simulate
	 * @param insertionMode
	 * @return the left itemstack, which could not be added
	 */
	private ItemStack addItems( final ItemStack itemsToAdd, final boolean modulate, final InsertionMode insertionMode )
	{
		if( itemsToAdd == null || itemsToAdd.stackSize == 0 )
		{
			return null;
		}

		final ItemStack left = itemsToAdd.copy();
		final int perOperationLimit = this.skipStackSizeCheck ?
                this.i.getInventoryStackLimit() : Math.min( this.i.getInventoryStackLimit(), itemsToAdd.getMaxStackSize() );
		final int inventorySize = this.i.getSizeInventory();

		// go over empty slots first if needed
		if (insertionMode != InsertionMode.DEFAULT)
		{
			for( int slot = 0; slot < inventorySize; slot++ )
			{
				final ItemStack next = left.copy();
				next.stackSize = Math.min( perOperationLimit, next.stackSize );

				if( this.i.isItemValidForSlot( slot, next ) && this.i.getStackInSlot( slot ) == null )
				{
					if( modulate )
					{
						this.i.setInventorySlotContents( slot, next );
						this.i.markDirty();
					}
					left.stackSize -= next.stackSize;

					if( left.stackSize <= 0 )
					{
						return null;
					}
				}
			}
		}

		// exit early if only empty slots are desired
		if (insertionMode == InsertionMode.ONLY_EMPTY)
		{
			return left;
		}

		for( int slot = 0; slot < inventorySize; slot++ )
		{
			final ItemStack next = left.copy();
			next.stackSize = Math.min( perOperationLimit, next.stackSize );

			if( this.i.isItemValidForSlot( slot, next ) )
			{
				final ItemStack is = this.i.getStackInSlot( slot );
				if( is == null )
				{
					left.stackSize -= next.stackSize;

					if( modulate )
					{
						this.i.setInventorySlotContents( slot, next );
						this.i.markDirty();
					}

					if( left.stackSize <= 0 )
					{
						return null;
					}
				}
				else if( Platform.isSameItemPrecise( is, left ) && is.stackSize < perOperationLimit )
				{
					final int room = perOperationLimit - is.stackSize;
					final int used = Math.min( left.stackSize, room );

					if( modulate )
					{
						is.stackSize += used;
						this.i.setInventorySlotContents( slot, is );
						this.i.markDirty();
					}

					left.stackSize -= used;
					if( left.stackSize <= 0 )
					{
						return null;
					}
				}
			}
		}

		return left;
	}

	private boolean canRemoveStackFromSlot( final int x, final ItemStack is )
	{
		if( this.wrapperEnabled )
		{
			return ( (IInventoryWrapper) this.i ).canRemoveItemFromSlot( x, is );
		}
		return true;
	}

	@Override
	public Iterator<ItemSlot> iterator()
	{
		return new InvIterator();
	}

	private class InvIterator implements Iterator<ItemSlot>
	{

		private final ItemSlot is = new ItemSlot();
		private int x = 0;

		@Override
		public boolean hasNext()
		{
			return this.x < AdaptorIInventory.this.i.getSizeInventory();
		}

		@Override
		public ItemSlot next()
		{
			final ItemStack iss = AdaptorIInventory.this.i.getStackInSlot( this.x );

			this.is.setExtractable( AdaptorIInventory.this.canRemoveStackFromSlot( this.x, iss ) );
			this.is.setItemStack( iss );

			this.is.setSlot( this.x );
			this.x++;
			return this.is;
		}

		@Override
		public void remove()
		{
			// nothing!
		}
	}
}
