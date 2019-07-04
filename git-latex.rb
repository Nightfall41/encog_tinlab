#!/usr/bin/env ruby

# A small ruby script to extract a git history to a tikz picture
# Author: Michael Hauspie <Michael.Hauspie@lifl.fr>
# Author: Lennart C. Karssen <lennart@karssen.org>
#
# Not clean code, not always working well, but does its job in most of
# the cases I needed :)
#
# LCK: Added some ideas from this tex.stackexchange answer:
#      http://tex.stackexchange.com/a/156501/7221
#
# To use this in a LaTeX document, the following packages need to be
# loaded in the preamble:
# - tikz
# - listings
# - xcolor, with dvipsnames option

require 'optparse'

# A commit object
class Commit
  attr_accessor :hash
  attr_accessor :children
  attr_accessor :parents
  attr_accessor :message
  attr_reader   :node_pos
  attr_reader   :node_color

  # Construct a commit from a line
  # A line is commit_hash [child1_hash ... childN_hash] message
  def initialize()
    @hash = nil
    @children = Hash.new()
    @parents = Hash.new()
    @message = ""
    @node_pos = 0
    @message_pos = 0
    # These colours require \usepackage[dvipsnames]{xcolor}
    @colours = ["ForestGreen", "Dandelion", "Red", "cyan", "magenta", "orange"]
  end
  def build(line)
    # Parse each word to match a hash or the commmit message
    pos=0
    line.split(" ").each do |word|
      if word =~ /[a-f0-9]{7}/ &&  @message == "" # match a short sha-1 commit tag
        if ! @hash
          @hash = word
        else
          @parents[word] = nil
        end
      elsif word == '*' # Position of the node
        @node_pos = pos
        @node_color = @colours[pos]
      elsif word =~ /[^|\/\\]/
        @message_pos = pos
        @message << " #{word}"
      end
      pos = pos + 1
    end
    @message.delete!("!")
    @message.lstrip!()
    if !@hash
      return false
    end
    return true
  end

  # sets a child object
  def set_parent(c)
    @parents[c.hash] = c
    c.children[@hash] = self
  end

  def export_to_tikz(ypos)
    puts "\\node[commit, #{node_color}, fill=#{node_color}] (#{@hash}) at (#{0.5*@node_pos},#{ypos}) {};"
    puts "\\node[right,xshift=10] (label_#{@hash}) at (#{@hash}.east) {\\verb!#{@hash}: #{@message}!};"
    @children.each_value do |child|
      puts "\\path[#{node_color}] (#{@hash}) to[out=90,in=-90] (#{child.hash});"
    end
  end

  def to_s()
    "#{@hash}: #{@message} #{@node_pos} #{@message_pos}"
  end
end

class Branch
  attr_accessor :name
  attr_accessor :hash
  attr_accessor :commit

  def initialize(line)
    words = line.split(" ")
    @name = words[0]
    @hash = words[1]
    @commit = nil
  end
end

# A repository, which is a collection of commit objects and branches
class Repository
  def initialize()
    @commits = Hash.new()
    @branches = Array.new()
  end

  def add_commit(commit)
    @commits[commit.hash] = commit
  end

  def add_branch(branch)
    if ! @commits.has_key?(branch.hash)
      return false
    end
    c = @commits.fetch(branch.hash)
    branch.commit = c
    @branches << branch
    return true
  end

  # This iterates other commits and resolves its parents
  def resolve_parents()
    @commits.each_value do |commit|
      commit.parents.keys.each do |parent_hash|
        c = @commits.fetch(parent_hash)
        commit.set_parent(c) # Link the commit object to its parent
      end
    end
  end

  def export_to_tikz
    puts "\\begin{tikzpicture}"
    puts "\\tikzstyle{commit}=[draw,circle,fill=white,inner sep=0pt,minimum size=5pt]"
    puts "\\tikzstyle{every path}=[draw]"
    puts "\\tikzstyle{branch}=[draw,rectangle,rounded corners=3,fill=white,inner sep=2pt,minimum size=5pt]"
    ypos=0
    ystep=-0.5
    @commits.each_value do |commit|
      commit.export_to_tikz(ypos)
      ypos = ypos + ystep
    end
    @branches.each do |branch|
      puts "\\node[branch,right,xshift=10] (#{branch.name}) at (label_#{branch.hash}.east) {\\lstinline{#{branch.name}}};"
    end
    puts "\\end{tikzpicture}"
  end
end




r = Repository.new()

# Start parsing command line options
# This hash will hold the options parsed from the command line by
# OptionParser.
options = {}

optparse = OptionParser.new do|opts|
  # Explain usage of this script
  opts.banner =
    "Usage: git-log-to-tikz.rb [options]
This script has to be run in a Git repository.
"

  # Define the options and some explanation
  options[:tophash] = "HEAD"
  opts.on( '-t', '--tophash HASH',
           'Hash of the top-most commit to include') do |h|
    options[:tophash] = h
  end
end

# Parse the command-line. The 'parse!' method parses ARGV and removes
# any options found there, as well as any parameters for the options.
optparse.parse!


# Extract commits
if options[:tophash] != "HEAD"
  # If a top hash was entered, don't use the --branches option or it
  # will output all commits.
  # Note: When using -t, the labels for branches are not added.
  cmd = "git log " +
        options[:tophash] +
        " --graph --oneline --parents"
else
  cmd = "git log --branches --graph --oneline --parents"
end

`#{cmd}`.lines().each do |line|
  c = Commit.new()
  if c.build(line)
    r.add_commit(c)
  end
end
r.resolve_parents()

# Extract branches
`git branch -av | cut -b 3-`.lines().each do |line|
  r.add_branch(Branch.new(line))
end

r.export_to_tikz()
