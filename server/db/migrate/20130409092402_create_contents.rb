class CreateContents < ActiveRecord::Migration
  def change
    create_table :contents do |t|
      t.string :title
      t.decimal :lat, precision:15, scale:10
      t.decimal :lng, precision:15, scale:10
      t.string :qtree_index

      t.timestamps
    end
  end
end
