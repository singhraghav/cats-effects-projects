package services

import domain.item.{CreateItemParam, Item, UpdateItemBrandParam, UpdateItemCategoryParam, UpdateItemNameParam, UpdateItemQuantityParam}

trait Items[F[_]] {

  def create(item: CreateItemParam): F[String]

  def updateName(param: UpdateItemNameParam): F[Unit]

  def updateBrand(param: UpdateItemBrandParam): F[Unit]

  def addNewCategories(param: UpdateItemCategoryParam): F[Unit]

  def removeCategories(param: UpdateItemCategoryParam): F[Unit]

  def updateQuantity(param: UpdateItemQuantityParam): F[Int]

  def findAll(): F[List[Item]]

}

