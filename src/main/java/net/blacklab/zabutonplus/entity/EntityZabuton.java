package net.blacklab.zabutonplus.entity;

import io.netty.buffer.ByteBuf;

import java.util.Iterator;
import java.util.List;

import net.blacklab.zabutonplus.ZabutonPlus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityZabuton extends EntityBoat implements IEntityAdditionalSpawnData {

	protected double zabutonX;
	protected double zabutonY;
	protected double zabutonZ;
	protected double zabutonYaw;
	protected double zabutonPitch;
	protected double velocityX;
	protected double velocityY;
	protected double velocityZ;
	protected int health;
	public boolean dispensed;
	public byte color;

	protected int boatPosRotationIncrements;

	// Method
	public EntityZabuton(World world) {
		super(world);
		preventEntitySpawning = true;
		setSize(0.81F, 0.2F);
//		yOffset = 0F;
		health = 20;
		dispensed = false;
		color = (byte)0xFF;
	}

	public EntityZabuton(World world, byte pColor) {
		this(world);
		color = pColor;
	}

	public EntityZabuton(World world, ItemStack itemstack) {
		this(world, (byte)(itemstack.getItemDamage() & 0x0f));
	}

	public EntityZabuton(World world, double x, double y, double z, byte pColor) {
		this(world, pColor);
		setPositionAndRotation(x, y + getYOffset(), z, 0F, 0F);
		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	protected void entityInit() {
		dataWatcher.addObject(17, new Byte((byte)(dispensed ? 0x01 : 0x00)));
		dataWatcher.addObject(18, Integer.valueOf(0));
		dataWatcher.addObject(19, new Byte((byte)0xFF));
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity par1Entity) {
		return par1Entity.getEntityBoundingBox();
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return getEntityBoundingBox();
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		color = nbttagcompound.getByte("Color");
		health = nbttagcompound.getShort("Health");
	}
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setByte("Color", (byte)(color & 0x0f));
		nbttagcompound.setShort("Health", (byte)health);
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		data.writeByte(color);
		data.writeFloat(rotationYaw);
	}
	@Override
	public void readSpawnData(ByteBuf data) {
		color = data.readByte();
		setRotation(data.readFloat(), 0.0F);
	}

	@Override
	public double getMountedYOffset() {
		if (riddenByEntity instanceof EntitySpider) {
			return -0.1D;
		}
		if (riddenByEntity instanceof EntityZombie ||
				riddenByEntity instanceof EntityEnderman) {
			return -0.4D;
		}

		return 0.1D;
	}

	@Override
	public boolean handleWaterMovement() {
		// 独自の水没判定
		int var4 = MathHelper.floor_double(getEntityBoundingBox().minX);
		int var5 = MathHelper.floor_double(getEntityBoundingBox().maxX + 1.0D);
		int var6 = MathHelper.floor_double(getEntityBoundingBox().minY);
		int var7 = MathHelper.floor_double(getEntityBoundingBox().maxY + 1.0D);
		int var8 = MathHelper.floor_double(getEntityBoundingBox().minZ);
		int var9 = MathHelper.floor_double(getEntityBoundingBox().maxZ + 1.0D);

		/*if (!worldObj.checkChunksExist(var4, var6, var8, var5, var7, var9)) {
			return false;
		} else */{
			boolean var10 = false;

			for (int var12 = var4; var12 < var5; ++var12) {
				for (int var13 = var6; var13 < var7; ++var13) {
					for (int var14 = var8; var14 < var9; ++var14) {
						Block var15 = worldObj.getBlockState(new BlockPos(var12, var13, var14)).getBlock();

						if (var15 != null && var15.getMaterial() == Material.water) {
							inWater = true;
							double var16 = var13 + 1 - BlockLiquid.getLiquidHeightPercent((Integer) worldObj.getBlockState(new BlockPos(var12, var13, var14)).getValue(BlockLiquid.LEVEL));

							if (var7 >= var16) {
								var10 = true;
							}
						} else {
							inWater = false;
						}
					}
				}
			}
			return var10;
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float pDammage) {
		Entity entity = damagesource.getEntity();
		if(worldObj.isRemote || isDead) {
			return true;
		}
		setBeenAttacked();
		if (entity instanceof EntityPlayer) {
			if(color >=0 && color < 16 && !((EntityPlayer)entity).capabilities.isCreativeMode)
			{
				entityDropItem(new ItemStack(ZabutonPlus.zabuton, 1, color), 0.0F);
			}
			setDead();
		} else {
			health -= pDammage;
			if(health <= 0) {
				setDead();
			}
		}
		if (isDead && riddenByEntity != null) {
			riddenByEntity.mountEntity(null);
			setRiddenByEntityID(riddenByEntity);
		}
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}

	@Override
	public void setVelocity(double d, double d1, double d2) {
		velocityX = motionX = d;
		velocityY = motionY = d1;
		velocityZ = motionZ = d2;
	}

	@Override
	public void onUpdate(){
		super.onEntityUpdate();

		// クライアントへはパケットで送ってたと思われる。dataWatcherに切り替え。
		if(!worldObj.isRemote) {
			dataWatcher.updateObject(19, color);
		} else {
			color = dataWatcher.getWatchableObjectByte(19);
		}
		
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		// ボートの判定のコピー
		// ボートは直接サーバーと位置情報を同期させているわけではなく、予測位置計算系に値を渡している。
		// 因みにボートの座標同期間隔は結構長めなので動きが変。

		double horizontalVecSq = Math.sqrt(motionX * motionX + motionZ * motionZ);
		
		if (!worldObj.isRemote && isDispensed() && horizontalVecSq == 0d) {
			setDispensed(false);
		}

		if (worldObj.isRemote) {
			// Client
/*
			if (boatPosRotationIncrements > 0 && !isDispensed()) {
				correctX = posX + (zabutonX - posX) / boatPosRotationIncrements;
				correctY = posY + (zabutonY - posY) / boatPosRotationIncrements;
				correctZ = posZ + (zabutonZ - posZ) / boatPosRotationIncrements;
				diffYaw = MathHelper.wrapAngleTo180_double(zabutonYaw - rotationYaw);
				rotationYaw = (float)(rotationYaw + diffYaw / boatPosRotationIncrements);
				rotationPitch = (float)(rotationPitch + (zabutonPitch - rotationPitch) / boatPosRotationIncrements);
				--boatPosRotationIncrements;
				setPosition(correctX, correctY, correctZ);
				setRotation(rotationYaw, rotationPitch);
			} else {
*/
				motionY = -0.08d;
				if (onGround && !isDispensed()) {
					motionX *= 0.5D;
					motionY *= 0.5D;
					motionZ *= 0.5D;
					// setVelocityの呼ばれる回数が少なくて変な動きをするので対策
//	                velocityChanged = true;
				}
				moveEntity(motionX, motionY, motionZ);

				motionX *= 0.9900000095367432D;
				motionY *= 0.949999988079071D;
				motionZ *= 0.9900000095367432D;
//			}
		} else {
			// Server
			// 落下
			motionY = -0.08d;

			// 最高速度判定
			Double lmaxspeed = isDispensed() ? 10.0D : 0.35D;
			if (horizontalVecSq > lmaxspeed) {
				double vectorCorrector = lmaxspeed / horizontalVecSq;
				motionX *= vectorCorrector;
				motionZ *= vectorCorrector;
				horizontalVecSq = lmaxspeed;
			}
			if (onGround && !isDispensed()) {
				motionX *= 0.5D;
				motionY *= 0.5D;
				motionZ *= 0.5D;
				// setVelocityの呼ばれる回数が少なくて変な動きをするので対策
//                velocityChanged = true;
			}
			moveEntity(motionX, motionY, motionZ);

			motionX *= 0.9900000095367432D;
			motionY *= 0.949999988079071D;
			motionZ *= 0.9900000095367432D;

			// ヘッディング
			rotationPitch = 0.0F;
			double diffX = prevPosX - posX;
			double diffZ = prevPosZ - posZ;

			if (horizontalVecSq > 0.001D) {
				horizontalVecSq = ((float)(Math.atan2(diffX, diffZ) * 180.0D / Math.PI));
			}

			double var14 = MathHelper.wrapAngleTo180_double(rotationYaw - rotationYaw);
			if (var14 > 20.0D) {
				var14 = 20.0D;
			}
			if (var14 < -20.0D) {
				var14 = -20.0D;
			}

			rotationYaw = (float)(rotationYaw + var14);
			setRotation(rotationYaw, rotationPitch);

			// 当たり判定
			List var16 = worldObj.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(0.17D, 0.0D, 0.17D));
			if (var16 != null && !var16.isEmpty()) {
				Iterator var28 = var16.iterator();

				while (var28.hasNext()) {
					Entity var18 = (Entity)var28.next();

					if (var18 != riddenByEntity && var18.canBePushed() && var18 instanceof EntityZabuton) {
						var18.applyEntityCollision(this);
					}
				}
			}
		}
		if (riddenByEntity != null) {
			if (riddenByEntity instanceof EntityPlayer) {
				setRotation(riddenByEntity.prevRotationYaw, rotationPitch);
				posX += riddenByEntity.motionX * 0.5d;
				posZ += riddenByEntity.motionZ * 0.5d;
				setPosition(posX, posY, posZ);
			}
			if (riddenByEntity instanceof EntityMob) {
				// 座ってる間は消滅させない
				setEntityLivingAge((EntityLivingBase)riddenByEntity, 0);
			}
			if (riddenByEntity.isDead) {
				// 着座対象が死んだら無人化
				riddenByEntity = null;
				setRiddenByEntityID(riddenByEntity);
			}
			else if (inWater) {
				// ぬれた座布団はひゃぁってなる
				riddenByEntity.mountEntity(null);
				setRiddenByEntityID(riddenByEntity);
			}
		}
	}

	public void setEntityLivingAge(EntityLivingBase entity, int a)
	{
		ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, entity, a, "field_70708_bq","entityAge");
	}

	@Override
	public void applyEntityCollision(Entity entity) {
		// 吸着判定
		if (worldObj.isRemote) {
			return;
		}
		if (entity == riddenByEntity) {
			return;
		}
		if ((entity instanceof EntityLiving) && !(entity instanceof EntityPlayer) && riddenByEntity == null && entity.ridingEntity == null) {
			entity.mountEntity(this);
			setRiddenByEntityID(riddenByEntity);
		}
		super.applyEntityCollision(entity);
	}

	@Override
	public boolean interactFirst(EntityPlayer entityplayer) {
		// ラーイド・オン！
		ZabutonPlus.Debug("INTERACT");
		if (riddenByEntity instanceof EntityPlayer && riddenByEntity != entityplayer) {
			return true;
		}
		if (!worldObj.isRemote) {
			entityplayer.mountEntity(this);
		}
		return true;
	}

	// 射出判定
	public boolean isDispensed() {
		return dataWatcher.getWatchableObjectByte(17) > 0x00;
	}

	public void setDispensed(boolean isDispensed) {
		dispensed = isDispensed;
		dataWatcher.updateObject(17, (byte)(isDispensed ? 0x01 : 0x00));
	}

	@Override
	public void updateRiderPosition() {
		if (riddenByEntity != null) {
			riddenByEntity.setPosition(posX, posY + getMountedYOffset() + riddenByEntity.getYOffset(), posZ);
		}
	}

	// クライアント側補正用
	public int getRiddenByEntityID() {
		int li = dataWatcher.getWatchableObjectInt(18);
		return li;
	}

	public Entity getRiddenByEntity() {
		return ((WorldClient)worldObj).getEntityByID(getRiddenByEntityID());
	}

	public void setRiddenByEntityID(Entity pentity) {
		dataWatcher.updateObject(18, Integer.valueOf(pentity == null ? 0 : pentity.getEntityId()));
	}

}