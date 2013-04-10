class Rest::ContentController < ApplicationController
  def create
    @content = Content.create!(title:params[:title],lat:params[:lat],lng:params[:lng])

    respond_to do |format|
      if @content.save
        format.json { render json: {status: 'ok', content: @content}}
      else
        format.json { render json: {status: 'fail', content: @content.errors} }
      end
    end
  end
end
