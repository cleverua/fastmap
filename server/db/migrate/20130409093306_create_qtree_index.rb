class CreateQtreeIndex < ActiveRecord::Migration
  def up
    add_index :contents, [:qtree_index]
  end

  def down
    remove_index :contents, [:qtree_index]
  end
end
