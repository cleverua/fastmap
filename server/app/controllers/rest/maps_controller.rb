class Rest::MapsController < ApplicationController

  before_filter :load_content

  QTREE_GROUPS = 4
  MAX_GROUPS_ZOOM_LEVEL = 11
  MIN_CLUSTER_SIZE = 6 # will not create cluster for elements less that MIN_CLUSTER_SIZE


  def show
    respond_to do |format|
      format.json {
        Rails.cache.write(request.path, render_to_string(json: {status: "ok", content: @content}), raw: true)
        render json: {status: "ok", content: @content}
      }
    end
  end
  private

  def load_content

  end
end
